# frozen_string_literal: true

require "dependabot/dependency"
require "dependabot/shared_helpers"
require "dependabot/errors"
require "dependabot/npm_and_yarn/update_checker"
require "dependabot/npm_and_yarn/file_parser"
require "dependabot/npm_and_yarn/version"
require "dependabot/npm_and_yarn/native_helpers"
require "dependabot/npm_and_yarn/file_updater/npmrc_builder"
require "dependabot/npm_and_yarn/file_updater/package_json_preparer"
require "dependabot/npm_and_yarn/sub_dependency_files_filterer"

module Dependabot
  module NpmAndYarn
    class UpdateChecker
      class SubdependencyVersionResolver
        def initialize(dependency:, credentials:, dependency_files:,
                       ignored_versions:, latest_allowable_version:)
          @dependency = dependency
          @credentials = credentials
          @dependency_files = dependency_files
          @ignored_versions = ignored_versions
          @latest_allowable_version = latest_allowable_version
        end

        def latest_resolvable_version
          raise "Not a subdependency!" if dependency.requirements.any?
          return if bundled_dependency?

          SharedHelpers.in_a_temporary_directory do
            write_temporary_dependency_files

            updated_lockfiles = filtered_lockfiles.map do |lockfile|
              updated_content = update_subdependency_in_lockfile(lockfile)
              updated_lockfile = lockfile.dup
              updated_lockfile.content = updated_content
              updated_lockfile
            end

            version_from_updated_lockfiles(updated_lockfiles)
          end
        rescue SharedHelpers::HelperSubprocessFailed
          # TODO: Move error handling logic from the FileUpdater to this class

          # Return nil (no update possible) if an unknown error occurred
          nil
        end

        private

        attr_reader :dependency, :credentials, :dependency_files,
                    :ignored_versions, :latest_allowable_version

        def update_subdependency_in_lockfile(lockfile)
          lockfile_name = Pathname.new(lockfile.name).basename.to_s
          path = Pathname.new(lockfile.name).dirname.to_s

          updated_files = if lockfile.name.end_with?("yarn.lock")
                            run_yarn_updater(path, lockfile_name)
                          else
                            run_npm_updater(path, lockfile_name)
                          end

          updated_files.fetch(lockfile_name)
        end

        def version_from_updated_lockfiles(updated_lockfiles)
          updated_files = dependency_files -
                          yarn_locks -
                          package_locks -
                          shrinkwraps +
                          updated_lockfiles

          updated_version = NpmAndYarn::FileParser.new(
            dependency_files: updated_files,
            source: nil,
            credentials: credentials
          ).parse.find { |d| d.name == dependency.name }&.version
          return unless updated_version

          version_class.new(updated_version)
        end

        def run_yarn_updater(path, lockfile_name)
          SharedHelpers.with_git_configured(credentials: credentials) do
            Dir.chdir(path) do
              SharedHelpers.run_helper_subprocess(
                command: NativeHelpers.helper_path,
                function: "yarn:updateSubdependency",
                args: [Dir.pwd, lockfile_name]
              )
            end
          end
        rescue SharedHelpers::HelperSubprocessFailed => e
          unfindable_str = "find package \"#{dependency.name}"
          raise unless e.message.include?("The registry may be down") ||
                       e.message.include?("ETIMEDOUT") ||
                       e.message.include?("ENOBUFS") ||
                       e.message.include?(unfindable_str)

          retry_count ||= 0
          retry_count += 1
          raise if retry_count > 2

          sleep(rand(3.0..10.0)) && retry
        end

        def run_npm_updater(path, lockfile_name)
          SharedHelpers.with_git_configured(credentials: credentials) do
            Dir.chdir(path) do
              SharedHelpers.run_helper_subprocess(
                command: NativeHelpers.helper_path,
                function: "npm:updateSubdependency",
                args: [Dir.pwd, lockfile_name, [dependency.to_h]]
              )
            end
          end
        end

        def write_temporary_dependency_files
          write_lock_files

          File.write(".npmrc", npmrc_content)

          package_files.each do |file|
            path = file.name
            FileUtils.mkdir_p(Pathname.new(path).dirname)
            File.write(file.name, prepared_package_json_content(file))
          end
        end

        def write_lock_files
          yarn_locks.each do |f|
            FileUtils.mkdir_p(Pathname.new(f.name).dirname)
            File.write(f.name, prepared_yarn_lockfile_content(f.content))
          end

          [*package_locks, *shrinkwraps].each do |f|
            FileUtils.mkdir_p(Pathname.new(f.name).dirname)
            File.write(f.name, f.content)
          end
        end

        # Duplicated in NpmLockfileUpdater
        # Remove the dependency we want to update from the lockfile and let
        # yarn find the latest resolvable version and fix the lockfile
        def prepared_yarn_lockfile_content(content)
          content.gsub(/^#{Regexp.quote(dependency.name)}\@.*?\n\n/m, "")
        end

        def prepared_package_json_content(file)
          NpmAndYarn::FileUpdater::PackageJsonPreparer.new(
            package_json_content: file.content
          ).prepared_content
        end

        def npmrc_content
          NpmAndYarn::FileUpdater::NpmrcBuilder.new(
            credentials: credentials,
            dependency_files: dependency_files
          ).npmrc_content
        end

        def version_class
          NpmAndYarn::Version
        end

        def package_locks
          @package_locks ||=
            dependency_files.
            select { |f| f.name.end_with?("package-lock.json") }
        end

        def yarn_locks
          @yarn_locks ||=
            dependency_files.
            select { |f| f.name.end_with?("yarn.lock") }
        end

        def shrinkwraps
          @shrinkwraps ||=
            dependency_files.
            select { |f| f.name.end_with?("npm-shrinkwrap.json") }
        end

        def lockfiles
          [*package_locks, *shrinkwraps, *yarn_locks]
        end

        def filtered_lockfiles
          @filtered_lockfiles ||=
            SubDependencyFilesFilterer.new(
              dependency_files: dependency_files,
              updated_dependencies: [updated_dependency]
            ).files_requiring_update
        end

        def updated_dependency
          Dependabot::Dependency.new(
            name: dependency.name,
            version: latest_allowable_version,
            previous_version: dependency.version,
            requirements: [],
            package_manager: dependency.package_manager
          )
        end

        def package_files
          @package_files ||=
            dependency_files.
            select { |f| f.name.end_with?("package.json") }
        end

        # TODO: We should try and fix this by updating the parent that's not
        # bundled. For this case: `chokidar > fsevents > node-pre-gyp > tar` we
        # would need to update `fsevents`
        #
        # We shouldn't update bundled sub-dependencies as they have been bundled
        # into the release at an exact version by a parent using
        # `bundledDependencies`.
        #
        # For example, fsevents < 2 bundles node-pre-gyp meaning all it's
        # sub-dependencies get bundled into the release tarball at publish time
        # so you always get the same sub-dependency versions if you re-install a
        # specific version of fsevents.
        #
        # Updating the sub-dependency by deleting the entry works but it gets
        # removed from the bundled set of dependencies and moved top level
        # resulting in a bunch of package duplication which is pretty confusing.
        def bundled_dependency?
          dependency.subdependency_metadata&.
            any? { |h| h.fetch(:npm_bundled, false) } ||
            false
        end
      end
    end
  end
end

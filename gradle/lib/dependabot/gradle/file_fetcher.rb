# frozen_string_literal: true

require "dependabot/file_fetchers"
require "dependabot/file_fetchers/base"

module Dependabot
  module Gradle
    class FileFetcher < Dependabot::FileFetchers::Base
      require_relative "file_fetcher/settings_file_parser"

      def self.required_files_in?(filenames)
        filenames.any? { |filename| supported_build_file_names.include?() }
      end

      def self.required_files_message
        "Repo must contain a build.gradle / build.gradle.kts file."
      end

      private

      def fetch_files
        fetched_files = []
        fetched_files << buildfile
        fetched_files += subproject_buildfiles
        fetched_files += dependency_script_plugins
        fetched_files
      end

      def buildfile
        @buildfile ||= begin
          file = supported_build_file
          fetch_file_from_host(file.name) if file
        end
      end

      def subproject_buildfiles
        return [] unless settings_file

        subproject_paths =
          SettingsFileParser.
          new(settings_file: settings_file).
          subproject_paths

        buildfile_paths = subproject_paths.flat_map do |d|
          supported_build_files(dir: d).map { |f| File.join(d, f.name) }
        end

        buildfile_paths.map do |path|
          fetch_file_from_host(path)
        rescue Dependabot::DependencyFileNotFound
          # Gradle itself doesn't worry about missing subprojects, so we don't
          nil
        end.compact
      end

      def dependency_script_plugins
        dependency_plugin_paths =
          buildfile.content.
          scan(/apply from:\s+['"]([^'"]+)['"]/).flatten.
          reject { |path| path.include?("://") }.
          reject { |path| !path.include?("/") && path.split(".").count > 2 }.
          select { |filename| filename.include?("dependencies") }.
          map { |path| path.gsub("$rootDir", ".") }.
          uniq

        dependency_plugin_paths.map do |path|
          fetch_file_from_host(path)
        rescue Dependabot::DependencyFileNotFound
          next nil if file_exists_in_submodule?(path)
          next nil if path.include?("${")

          raise
        end.compact
      end

      def file_exists_in_submodule?(path)
        fetch_file_from_host(path, fetch_submodules: true)
        true
      rescue Dependabot::DependencyFileNotFound
        false
      end

      def settings_file
        @settings_file ||= begin
          file = supported_settings_file
          fetch_file_from_host(file.name) if file
        rescue Dependabot::DependencyFileNotFound
          nil
        end
      end

      def supported_build_file(dir: ".")
        supported_file(dir: dir, supported_file_names: supported_build_file_names)
      end

      def supported_build_files(dir: ".")
        supported_files(dir: dir, supported_file_names: supported_build_file_names)
      end

      def supported_settings_file(dir: ".")
        supported_file(dir: dir, supported_file_names: supported_settings_file_names)
      end

      def supported_build_file_names
        ["build.gradle", "build.gradle.kts"]
      end

      def supported_settings_file_names
        ["settings.gradle", "settings.gradle.kts"]
      end

      def supported_file(dir:, supported_file_names:)
        repo_contents(dir: dir).find { |f| supported_file_names.include?(f.name) }
      end

      def supported_files(dir:, supported_file_names:)
        repo_contents(dir: dir).select { |f| supported_file_names.include?(f.name) }
      end
    end
  end
end

Dependabot::FileFetchers.register("gradle", Dependabot::Gradle::FileFetcher)

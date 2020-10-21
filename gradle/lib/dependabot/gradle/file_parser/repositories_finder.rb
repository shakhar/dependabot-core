# frozen_string_literal: true

require "dependabot/gradle/file_parser"

module Dependabot
  module Gradle
    class FileParser
      class RepositoriesFinder
        # The Central Repo doesn't have special status for Gradle, but until
        # we're confident we're selecting repos correctly it's wise to include
        # it as a default.
        CENTRAL_REPO_URL = "https://repo.maven.apache.org/maven2"

        REPOSITORIES_BLOCK_START = /(?:^|\s)repositories\s*\{/.freeze

        GROOVY_MAVEN_REPO_REGEX =
          /maven\s*\{[^\}]*\surl[\s\(]\s*['"](?<url>[^'"]+)['"]/.freeze

        KOTLIN_MAVEN_REPO_REGEX =
          /maven\(['"](?<url>[^'"]+)['"]\)/.freeze

        MAVEN_REPO_REGEX =
          /(#{KOTLIN_MAVEN_REPO_REGEX}|#{GROOVY_MAVEN_REPO_REGEX})/.freeze

        def initialize(dependency_files:, target_dependency_file:)
          @dependency_files = dependency_files
          @target_dependency_file = target_dependency_file
          raise "No target file!" unless target_dependency_file
        end

        def repository_urls
          repository_urls = []
          repository_urls += inherited_repository_urls
          repository_urls += own_buildfile_repository_urls
          repository_urls = repository_urls.uniq

          return repository_urls unless repository_urls.empty?

          [CENTRAL_REPO_URL]
        end

        private

        attr_reader :dependency_files, :target_dependency_file

        def inherited_repository_urls
          return [] unless top_level_buildfile

          buildfile_content = comment_free_content(top_level_buildfile)
          subproject_blocks = []

          buildfile_content.scan(/(?:^|\s)allprojects\s*\{/) do
            mtch = Regexp.last_match
            subproject_blocks <<
              mtch.post_match[0..closing_bracket_index(mtch.post_match)]
          end

          if top_level_buildfile != target_dependency_file
            buildfile_content.scan(/(?:^|\s)subprojects\s*\{/) do
              mtch = Regexp.last_match
              subproject_blocks <<
                mtch.post_match[0..closing_bracket_index(mtch.post_match)]
            end
          end

          repository_urls_from(subproject_blocks.join("\n"))
        end

        def own_buildfile_repository_urls
          buildfile_content = comment_free_content(target_dependency_file)

          buildfile_content.dup.scan(/(?:^|\s)subprojects\s*\{/) do
            mtch = Regexp.last_match
            buildfile_content.gsub!(
              mtch.post_match[0..closing_bracket_index(mtch.post_match)],
              ""
            )
          end

          repository_urls_from(buildfile_content)
        end

        def repository_urls_from(buildfile_content)
          repository_urls = []

          repository_blocks = []
          buildfile_content.scan(REPOSITORIES_BLOCK_START) do
            mtch = Regexp.last_match
            repository_blocks <<
              mtch.post_match[0..closing_bracket_index(mtch.post_match)]
          end

          repository_blocks.each do |block|
            if block.match?(/\sgoogle\(/)
              repository_urls << "https://maven.google.com/"
            end

            if block.match?(/\smavenCentral\(/)
              repository_urls << "https://repo.maven.apache.org/maven2/"
            end

            if block.match?(/\sjcenter\(/)
              repository_urls << "https://jcenter.bintray.com/"
            end

            block.scan(MAVEN_REPO_REGEX) do
              repository_urls << Regexp.last_match.named_captures.fetch("url")
            end
          end

          repository_urls.
            map { |url| url.strip.gsub(%r{/$}, "") }.
            select { |url| valid_url?(url) }.
            uniq
        end

        def closing_bracket_index(string)
          closes_required = 1

          string.chars.each_with_index do |char, index|
            closes_required += 1 if char == "{"
            closes_required -= 1 if char == "}"
            return index if closes_required.zero?
          end

          0
        end

        def valid_url?(url)
          # Reject non-http URLs because they're probably parsing mistakes
          return false unless url.start_with?("http")

          URI.parse(url)
          true
        rescue URI::InvalidURIError
          false
        end

        def comment_free_content(buildfile)
          buildfile.content.
            gsub(%r{(?<=^|\s)//.*$}, "\n").
            gsub(%r{(?<=^|\s)/\*.*?\*/}m, "")
        end

        def top_level_buildfile
          @top_level_buildfile ||=
            dependency_files.find { |f| supported_build_file_names.include?(f.name) }
        end

        def supported_build_file_names
          ["build.gradle", "build.gradle.kts"]
        end
      end
    end
  end
end

# frozen_string_literal: true

require "spec_helper"
require "dependabot/dependency_file"
require "dependabot/gradle/file_parser/property_value_finder"

RSpec.configure do |config|
  config.filter_run_when_matching :focus
end

RSpec.describe Dependabot::Gradle::FileParser::PropertyValueFinder do
  let(:finder) { described_class.new(dependency_files: dependency_files) }

  let(:dependency_files) { [buildfile] }
  let(:buildfile) do
    Dependabot::DependencyFile.new(
      name: "build.gradle.kts",
      content: fixture("buildfiles", "kotlin", buildfile_fixture_name)
    )
  end
  let(:buildfile_fixture_name) { "single_property_build.gradle.kts" }

  describe "#property_details" do
    subject(:property_details) do
      finder.property_details(
        property_name: property_name,
        callsite_buildfile: callsite_buildfile
      )
    end

    context "with a single buildfile" do
      context "when the property is declared in the calling buildfile" do
        let(:buildfile_fixture_name) { "single_property_build.gradle.kts" }
        let(:property_name) { "kotlinVersion" }
        let(:callsite_buildfile) { buildfile }
        its([:value]) { is_expected.to eq("1.1.4-3") }
        its([:declaration_string]) do
          is_expected.to eq("extra[\"kotlinVersion\"] = \"1.1.4-3\"")
        end
        its([:file]) { is_expected.to eq("build.gradle.kts") }

        context "and the property name has a `project.` prefix" do
          let(:property_name) { "project.kotlinVersion" }
          its([:value]) { is_expected.to eq("1.1.4-3") }
          its([:file]) { is_expected.to eq("build.gradle.kts") }
        end

        context "and the property name has a `rootProject.` prefix" do
          let(:property_name) { "rootProject.kotlinVersion" }
          its([:value]) { is_expected.to eq("1.1.4-3") }
          its([:file]) { is_expected.to eq("build.gradle.kts") }
        end

        context "and tricky properties" do
          let(:buildfile_fixture_name) { "properties.gradle.kts" }

          context "and the property is declared with extra[key] = value" do
            let(:property_name) { "kotlinVersion" }
            its([:value]) { is_expected.to eq("1.2.61") }
            its([:declaration_string]) do
              is_expected.to eq("extra[\"kotlinVersion\"] = \"1.2.61\"")
            end
          end

          context "and the property is declared with extra.set(key, value)" do
            let(:property_name) { "kotlinVersion" }
            its([:value]) { is_expected.to eq("1.2.61") }
            its([:declaration_string]) do
              is_expected.to eq("extra[\"kotlinVersion\"] = \"1.2.61\"")
            end
          end

          context "and the property is declared in an extra.apply block" do
            let(:property_name) { "buildToolsVersion" }
            its([:value]) { is_expected.to eq("27.0.3") }
            its([:declaration_string]) do
              is_expected.to eq("set(\"buildToolsVersion\", \"27.0.3\")")
            end

            context "and the property name has already been set" do
              let(:buildfile_fixture_name) { "duplicate_property_name.gradle.kts" }
              let(:property_name) { "spek_version" }
              its([:value]) { is_expected.to eq("2.0.6") }
              its([:declaration_string]) do
                is_expected.to eq("spek_version = \"2.0.6\"")
              end
            end
          end

          context "and the property is preceded by a comment" do
            # This is important because the declaration string must not include
            # whitespace that will be different to when the FileUpdater uses it
            # (i.e., before the comments are stripped out)
            let(:property_name) { "supportVersion" }
            its([:value]) { is_expected.to eq("27.1.1") }
            its([:declaration_string]) do
              is_expected.to eq("set(\"supportVersion\", \"27.1.1\")")
            end
          end

          context "and the property is using findProperty syntax" do
            let(:property_name) { "findPropertyVersion" }
            its([:value]) { is_expected.to eq("27.1.1") }
            its([:declaration_string]) do
              # rubocop:disable Layout/LineLength
              is_expected.to eq("set(\"findPropertyVersion\", project.findProperty(\"findPropertyVersion\") ?: \"27.1.1\")")
              # rubocop:enable Layout/LineLength
            end
          end

          context "and the property is using hasProperty syntax" do
            let(:property_name) { "hasPropertyVersion" }
            its([:value]) { is_expected.to eq("27.1.1") }
            its([:declaration_string]) do
              # rubocop:disable Layout/LineLength
              is_expected.to eq("set(\"hasPropertyVersion\", if(project.hasProperty(\"hasPropertyVersion\")) project.getProperty(\"hasPropertyVersion\") else \"27.1.1\")")
              # rubocop:enable Layout/LineLength
            end
          end

          context "and the property is commented out" do
            let(:property_name) { "commentedVersion" }
            it { is_expected.to be_nil }
          end

          context "and the property is declared within a namespace" do
            let(:buildfile_fixture_name) { "properties_namespaced.gradle.kts" }
            let(:property_name) { "versions.okhttp" }

            its([:value]) { is_expected.to eq("3.12.1") }
            its([:declaration_string]) do
              is_expected.to eq("okhttp                  = \"3.12.1\"")
            end
            context "and the property is using findProperty syntax" do
              let(:property_name) { "versions.findPropertyVersion" }
              its([:value]) { is_expected.to eq("1.0.0") }
              its([:declaration_string]) do
                # rubocop:disable Layout/LineLength
                is_expected.to eq("findPropertyVersion     = project.findProperty(\"findPropertyVersion\") ?: \"1.0.0\"")
                # rubocop:enable Layout/LineLength
              end
            end

            context "and the property is using hasProperty syntax" do
              let(:property_name) { "versions.hasPropertyVersion" }
              its([:value]) { is_expected.to eq("1.0.0") }
              its([:declaration_string]) do
                # rubocop:disable Layout/LineLength
                is_expected.to eq("hasPropertyVersion      = if(project.hasProperty(\"hasPropertyVersion\")) project.getProperty(\"hasPropertyVersion\") else \"1.0.0\"")
                # rubocop:enable Layout/LineLength
              end
            end
          end
        end
      end
    end

    context "with multiple buildfiles" do
      let(:dependency_files) { [buildfile, callsite_buildfile] }
      let(:buildfile_fixture_name) { "single_property_build.gradle.kts" }
      let(:property_name) { "kotlinVersion" }
      let(:callsite_buildfile) do
        Dependabot::DependencyFile.new(
          name: "myapp/build.gradle.kts",
          content: fixture("buildfiles", "kotlin", callsite_fixture_name)
        )
      end
      let(:callsite_fixture_name) { "basic_build.gradle.kts" }

      its([:value]) { is_expected.to eq("1.1.4-3") }
      its([:file]) { is_expected.to eq("build.gradle.kts") }

      context "and the property name has a `project.` prefix" do
        let(:property_name) { "project.kotlinVersion" }
        its([:value]) { is_expected.to eq("1.1.4-3") }
        its([:file]) { is_expected.to eq("build.gradle.kts") }
      end

      context "and the property name has a `rootProject.` prefix" do
        let(:property_name) { "rootProject.kotlinVersion" }
        its([:value]) { is_expected.to eq("1.1.4-3") }
        its([:file]) { is_expected.to eq("build.gradle.kts") }
      end

      context "with a property that only appears in the callsite buildfile" do
        let(:buildfile_fixture_name) { "basic_build.gradle.kts" }
        let(:callsite_fixture_name) { "single_property_build.gradle.kts" }

        context "and the property name has a `project.` prefix" do
          let(:property_name) { "project.kotlinVersion" }
          its([:value]) { is_expected.to eq("1.1.4-3") }
          its([:file]) { is_expected.to eq("myapp/build.gradle.kts") }
        end

        context "and the property name has a `rootProject.` prefix" do
          let(:property_name) { "rootProject.kotlinVersion" }
          # We wouldn\"t normally expect this to be `nil` - it\"s more likely to
          # be another version specified in the root project file.
          it { is_expected.to be_nil }
        end
      end
    end
  end
end

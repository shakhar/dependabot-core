# frozen_string_literal: true

require "spec_helper"
require "dependabot/gradle/file_fetcher"
require_common_spec "file_fetchers/shared_examples_for_file_fetchers"

RSpec.describe Dependabot::Gradle::FileFetcher do
  it_behaves_like "a dependency file fetcher"

  let(:source) do
    Dependabot::Source.new(
      provider: "github",
      repo: "gocardless/bump",
      directory: directory
    )
  end
  let(:file_fetcher_instance) do
    described_class.new(source: source, credentials: credentials)
  end
  let(:directory) { "/" }
  let(:github_url) { "https://api.github.com/" }
  let(:url) { github_url + "repos/gocardless/bump/contents/" }
  let(:credentials) do
    [{
      "type" => "git_source",
      "host" => "github.com",
      "username" => "x-access-token",
      "password" => "token"
    }]
  end

  before { allow(file_fetcher_instance).to receive(:commit).and_return("sha") }

  context "with a basic buildfile" do
    before do
      stub_request(:get, url + "?ref=sha").
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_with_subdir.json"),
            headers: { "content-type" => "application/json" }
          )
      stub_request(:get, File.join(url, "build.gradle?ref=sha")).
        with(headers: { "Authorization" => "token token" }).
        to_return(
          status: 200,
          body: fixture("github", "groovy", "contents_java_basic_buildfile.json"),
          headers: { "content-type" => "application/json" }
        )
      stub_request(:get, File.join(url, "settings.gradle?ref=sha")).
        with(headers: { "Authorization" => "token token" }).
        to_return(status: 404)
    end

    it "fetches the buildfile" do
      expect(file_fetcher_instance.files.count).to eq(1)
      expect(file_fetcher_instance.files.map(&:name)).
        to match_array(%w(build.gradle))
    end

    context "with a settings.gradle" do
      before do
        stub_request(:get, url + "?ref=sha").
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_with_subdir.json"),
            headers: { "content-type" => "application/json" }
          )
        stub_request(:get, url + "app?ref=sha").
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_with_subdir.json"),
            headers: { "content-type" => "application/json" }
          )
        stub_request(:get, File.join(url, "settings.gradle?ref=sha")).
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_simple_settings.json"),
            headers: { "content-type" => "application/json" }
          )
        stub_request(:get, File.join(url, "app/build.gradle?ref=sha")).
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_basic_buildfile.json"),
            headers: { "content-type" => "application/json" }
          )
      end

      it "fetches the main buildfile and subproject buildfile" do
        expect(file_fetcher_instance.files.count).to eq(2)
        expect(file_fetcher_instance.files.map(&:name)).
          to match_array(%w(build.gradle app/build.gradle))
      end

      context "when the subproject can't fe found" do
        before do
          stub_request(:get, File.join(url, "app/build.gradle?ref=sha")).
            with(headers: { "Authorization" => "token token" }).
            to_return(status: 404)
        end

        it "fetches the main buildfile" do
          expect(file_fetcher_instance.files.count).to eq(1)
          expect(file_fetcher_instance.files.map(&:name)).
            to match_array(%w(build.gradle))
        end
      end
    end
  end

  context "with a script plugin" do
    before do
      stub_request(:get, url + "?ref=sha").
        with(headers: { "Authorization" => "token token" }).
        to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_java_with_subdir.json"),
            headers: { "content-type" => "application/json" }
        )
      stub_request(:get, File.join(url, "build.gradle?ref=sha")).
        with(headers: { "Authorization" => "token token" }).
        to_return(
          status: 200,
          body: fixture(
            "github",
            "groovy",
            "contents_java_buildfile_with_script_plugins.json"
          ),
          headers: { "content-type" => "application/json" }
        )
      stub_request(:get, File.join(url, "settings.gradle?ref=sha")).
        with(headers: { "Authorization" => "token token" }).
        to_return(status: 404)
      stub_request(:get, File.join(url, "gradle/dependencies.gradle?ref=sha")).
        with(headers: { "Authorization" => "token token" }).
        to_return(
          status: 200,
          body: fixture("github", "groovy", "contents_java_simple_settings.json"),
          headers: { "content-type" => "application/json" }
        )
    end

    it "fetches the buildfile and the dependencies script" do
      expect(file_fetcher_instance.files.count).to eq(2)
      expect(file_fetcher_instance.files.map(&:name)).
        to match_array(%w(build.gradle gradle/dependencies.gradle))
    end

    context "that can't be found" do
      before do
        stub_request(:get, url + "?ref=sha").
          with(headers: { "Authorization" => "token token" }).
          to_return(
              status: 200,
              body: fixture("github", "groovy", "contents_java_with_subdir.json"),
              headers: { "content-type" => "application/json" }
          )
        stub_request(
          :get,
          File.join(url, "gradle/dependencies.gradle?ref=sha")
        ).with(headers: { "Authorization" => "token token" }).
          to_return(status: 404)

        stub_request(:get, File.join(url, "gradle?ref=sha")).
          with(headers: { "Authorization" => "token token" }).
          to_return(
            status: 200,
            body: fixture("github", "groovy", "contents_with_settings.json"),
            headers: { "content-type" => "application/json" }
          )
      end

      it "raises a DependencyFileNotFound error" do
        expect { file_fetcher_instance.files }.
          to raise_error(Dependabot::DependencyFileNotFound)
      end
    end
  end
end

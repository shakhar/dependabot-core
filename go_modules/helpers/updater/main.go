package updater

import (
	"io/ioutil"
	"strings"

	"github.com/dependabot/gomodules-extracted/cmd/go/_internal_/modfile"
	"github.com/dependabot/gomodules-extracted/cmd/go/_internal_/semver"
	"golang.org/x/tools/go/packages"
)

type Dependency struct {
	Name            string `json:"name"`
	Version         string `json:"version"`
	PreviousVersion string `json:"previous_version"`
	Indirect        bool   `json:"indirect"`
}

func (d *Dependency) majorUpgrade() bool {
	if d.PreviousVersion == "" {
		return false
	}
	return semver.Major(d.PreviousVersion) != semver.Major(d.Version)
}

func (d *Dependency) oldName() string {
	m := semver.Major(d.PreviousVersion)
	parts := strings.Split(d.Name, "/")
	root := strings.Join(parts[:len(parts)-1], "/")
	if m == "v0" || m == "v1" || m == "" {
		return root
	}
	return root + "/" + m
}

type Args struct {
	Dependencies []Dependency `json:"dependencies"`
}

func UpdateDependencyFile(args *Args) (interface{}, error) {
	data, err := ioutil.ReadFile("go.mod")
	if err != nil {
		return nil, err
	}

	f, err := modfile.Parse("go.mod", data, nil)
	if err != nil {
		return nil, err
	}

	for _, dep := range args.Dependencies {
		if dep.majorUpgrade() {
			f.DropRequire(dep.oldName())
		}

		f.AddRequire(dep.Name, dep.Version)
	}

	for _, r := range f.Require {
		for _, dep := range args.Dependencies {
			if r.Mod.Path == dep.Name {
				setIndirect(r.Syntax, dep.Indirect)
			}
		}
	}

	f.SortBlocks()
	f.Cleanup()

	newModFile, _ := f.Format()

	return string(newModFile), nil
}

// UpdateImportPaths traverses the packages in the current directory, and for
// each one parses the go syntax and swaps out import paths from the old to the
// new path.
func UpdateImportPaths(args *Args) (interface{}, error) {
	for _, dep := range args.Dependencies {
		if dep.majorUpgrade() {
			c := &packages.Config{Mode: packages.LoadSyntax, Tests: true, Dir: "./"}
			pkgs, err := packages.Load(c, "./...")
			if err != nil {
				return nil, err
			}

			ids := map[string]struct{}{}
			files := map[string]struct{}{}

			for _, p := range pkgs {
				if _, ok := ids[p.ID]; ok {
					continue
				}
				ids[p.ID] = struct{}{}
				err = updateImportPath(p, dep.oldName(), dep.Name, files)
				if err != nil {
					return nil, err
				}
			}
		}
	}

	return nil, nil
}

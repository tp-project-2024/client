{pkgs, ...}: let
  name = "intellij-kotlin-env";
in
  (pkgs.buildFHSEnv {
    # The name of the environment and the wrapper executable.
    inherit name;
    # Packages to be installed for the main host's architecture
    targetPkgs = pkgs: (with pkgs; [
      jetbrains.idea-community
      kotlin
      libGL
    ]);
  })
  .env

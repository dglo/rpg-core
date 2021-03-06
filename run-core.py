#!/usr/bin/env python

import sys

from runner import JavaRunner

MAIN_CLASS = "org.glowacki.core.ascii.Runner"

#JAVA_ARGS = "-Xmx4000m"

SUBPROJECT_PKGS = ("core", )
REPO_PKGS = (
    #("log4j", "log4j", "1.2.7"),
    ("com/googlecode/lanterna", "lanterna", "2.1.3"),
    )

if __name__ == "__main__":
    runner = JavaRunner(MAIN_CLASS)

    runner.add_subproject_jars(SUBPROJECT_PKGS)
    runner.add_repo_jars(REPO_PKGS)

    try:
        java_args = JAVA_ARGS
    except NameError:
        java_args = None

    rundata = runner.run(sys.argv[1:], java_args)
    if rundata.returncode() is not None and rundata.returncode() != 0:
        raise SystemExit(rundata.returncode())

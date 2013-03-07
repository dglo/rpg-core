#!/usr/bin/env python

import datetime
import os
import select
import signal
import subprocess
import sys

class RunnerException(Exception):
    pass

class RunData(object):
    def __init__(self, mainClass, sysArgs, javaArgs):
        self.__exitsig = False
        self.__returncode = None
        self.__runTime = None
        self.__waitTime = None

        self.__cmd = [ "java", ]
        if javaArgs is not None:
            if type(javaArgs) == str:
                self.__cmd.append(javaArgs)
            elif type(javaArgs) == list or type(javaArgs) == tuple:
                self.__cmd += javaArgs
            else:
                raise RunnerException("Bad javaArgs type %s for %s" %
                                      (type(javaArgs), javaArgs))

        self.__cmd.append(mainClass)
        if sysArgs is not None:
            self.__cmd += sysArgs

    @classmethod
    def __timediff(cls, startTime, endTime):
        diff = endTime - startTime
        return float(diff.seconds) + \
            (float(diff.microseconds) / 1000000.0)

    def command(self):
        return self.__cmd

    def exitSignal(self):
        return self.__exitsig

    def killSignal(self):
        return self.__killsig

    def returncode(self):
        return self.__returncode

    def runTime(self):
        return self.__runTime

    def setExitSignal(self, val):
        self.__exitsig = val

    def setKillSignal(self, val):
        self.__killsig = val

    def setReturnCode(self, val):
        self.__returncode = val

    def setRunTime(self, startTime, endTime):
        self.__runTime = self.__timediff(startTime, endTime)

    def setWaitTime(self, startTime, endTime):
        self.__waitTime = self.__timediff(startTime, endTime)

    def waitTime(self):
        return self.__waitTime

class JavaRunner(object):
    def __init__(self, mainClass):
        self.__mainClass = mainClass
        self.__classes = []
        self.__classpath = None

        self.__proc = None
        self.__killsig = None
        self.__exitsig = None

    def __run_command(self, data, debug=False):
        self.__killsig = None
        self.__exitsig = None

        if debug:
            print " ".join(data.command())

        startTime = datetime.datetime.now()

        self.__proc = subprocess.Popen(data.command(), stdout=subprocess.PIPE,
                                       stderr=subprocess.PIPE,
                                       preexec_fn=os.setsid)
        while True:
            reads = [self.__proc.stdout.fileno(), self.__proc.stderr.fileno()]
            try:
                ret = select.select(reads, [], [])
            except select.error, err:
                pass # ignore a single interrupt

            for fd in ret[0]:
                if fd == self.__proc.stdout.fileno():
                    line = self.__proc.stdout.readline()
                    self.process(line, False)
                if fd == self.__proc.stderr.fileno():
                    line = self.__proc.stderr.readline()
                    self.process(line, True)

            if self.__proc.poll() != None:
                break

        self.__proc.stdout.close()
        self.__proc.stderr.close()

        endTime = datetime.datetime.now()

        self.__proc.wait()

        waitTime = datetime.datetime.now()

        data.setReturnCode(self.__proc.returncode)

        data.setRunTime(startTime, endTime)
        data.setWaitTime(endTime, waitTime)

        data.setExitSignal(self.__exitsig)
        data.setKillSignal(self.__killsig)

        self.__proc = None

    def add_subproject_jars(self, pkgs):
        curdir = os.getcwd()
        parent = os.path.dirname(curdir)

        for pkg in pkgs:
            jar = None

            for d in (curdir, parent):
                tmpjar = os.path.join(d, "target",
                                      pkg + "-1.0.0-SNAPSHOT.jar")
                if os.path.exists(tmpjar):
                    jar = tmpjar
                    break

                tmpjar = os.path.join(d, pkg, "target",
                                      pkg + "-1.0.0-SNAPSHOT.jar")
                if os.path.exists(tmpjar):
                    jar = tmpjar
                    break

            if jar is None:
                raise RunnerException("Cannot find %s jar file" % pkg)

            self.__classes.append(jar)
            self.__classpath = None

    def add_repo_jars(self, rpkgs):
        home = os.environ["HOME"]

        for (proj, name, version) in rpkgs:
            path = os.path.join(home, ".m2", "repository", proj, name, version,
                                "%s-%s.jar" % (name, version))
            if not os.path.exists(path):
                raise sys.exit(("Cannot find %s in local Maven repository" +
                                " (%s)") % (name, path))
            self.__classes.append(path)
            self.__classpath = None

    def kill(self, sig):
        self.sendSignal(sig, None)
        self.__killsig = sig

    def process(self, line, is_stderr=False):
        if not is_stderr:
            sys.stdout.write(line)
            sys.stdout.flush()
        else:
            sys.stderr.write(line)

    def quickExit(self, sig, frame):
        self.sendSignal(sig, frame)
        self.__exitsig = sig

    def returncode(self):
        return self.__returncode

    def run(self, sysArgs=None, javaArgs=None, debug=False):
        # set CLASSPATH if it hasn't been set yet
        if self.__classpath is None:
            self.__classpath = ":".join(self.__classes)
            os.environ["CLASSPATH"] = self.__classpath

            if debug:
                print "export CLASSPATH=\"%s\"" % os.environ["CLASSPATH"]

        signal.signal(signal.SIGINT, self.quickExit)
        signal.signal(signal.SIGQUIT, self.sendSignal)

        try:
            rundata = RunData(self.__mainClass, sysArgs, javaArgs)
            self.__run_command(rundata, debug)
        finally:
            signal.signal(signal.SIGINT, signal.SIG_DFL)
            signal.signal(signal.SIGQUIT, signal.SIG_DFL)

        return rundata

    def sendSignal(self, sig, frame):
        if self.__proc is not None:
            os.killpg(self.__proc.pid, sig)


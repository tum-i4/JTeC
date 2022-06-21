# Sample Scripts

This directory contains sample scripts.
Currently, we have the following examples:

- `frida-agent.py`: Simple [frida](https://frida.re/) agent that instruments all open system calls in the process (i.e.,
  test suite) it's gets attached to. Prerequisites: `pip install frida frida-tools`. This script is supposed to
  demonstrate the pre-test hook in practice.
- `java-package-analyzer.py`: Recursively searches for all distinct package names in a project. Useful if one wants to
  figure out, how to set the `cov.includes` option. 


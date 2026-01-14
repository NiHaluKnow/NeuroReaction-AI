#!/bin/bash
# Delete all compiled .class files in the current directory and its subdirectories

find . -name "*.class" -type f -delete

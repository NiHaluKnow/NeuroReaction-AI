#!/bin/bash

# Create virtual environment if it doesn't exist
if [ ! -d "myenv" ]; then
    python3 -m venv myenv
fi

# Activate the environment
source myenv/bin/activate

# Your commands go here
echo "Virtual environment activated!"
echo "Python path: $(which python)"
echo "Pip path: $(which pip)"
#!/bin/bash
set -e

# Run setup-model.sh if it exists
if [ -f ./setup-model.sh ]; then
  echo "Setting up model..."
  ./setup-model.sh &
fi

# Start the main process
echo "Starting API server..."
exec "$@"
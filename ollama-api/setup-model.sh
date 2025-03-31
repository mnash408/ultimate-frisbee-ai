#!/bin/bash
echo "Waiting for Ollama service to be ready..."
until curl -s ${OLLAMA_HOST}/api/tags > /dev/null; do
  sleep 1
done

echo "Checking if ultimate-frisbee model exists..."
if ! curl -s ${OLLAMA_HOST}/api/tags | grep -q "ultimate-frisbee"; then
  echo "Creating ultimate-frisbee model..."
  
  # Create Modelfile
  cat > Modelfile << EOL
FROM mistral:7b-instruct

SYSTEM """
You are an Ultimate Frisbee AI assistant that helps players, coaches and enthusiasts with
questions about Ultimate Frisbee rules, strategies, training techniques, and history.
Your goal is to provide accurate, helpful information about Ultimate Frisbee.
"""
EOL

  # Create the model
  curl -X POST ${OLLAMA_HOST}/api/create -d '{
    "name": "ultimate-frisbee",
    "modelfile": "FROM mistral:7b-instruct\n\nSYSTEM \"\"\"You are an Ultimate Frisbee AI assistant that helps players, coaches and enthusiasts with questions about Ultimate Frisbee rules, strategies, training techniques, and history. Your goal is to provide accurate, helpful information about Ultimate Frisbee.\"\"\""
  }'
  
  echo "Model created successfully!"
else
  echo "ultimate-frisbee model already exists."
fi

echo "Starting Ollama API wrapper..."
node ollama-api.js
// ollama-api.js
const express = require('express');
const axios = require('axios');
const cors = require('cors');
const app = express();
const PORT = 8081;

// Get Ollama host from environment variable or use default
const OLLAMA_HOST = process.env.OLLAMA_HOST || 'http://localhost:11434';

app.use(cors());
app.use(express.json());

app.post('/api/llm', async (req, res) => {
  try {
    const { question, context } = req.body;
    
    // Combine context and question
    const prompt = `
Context about Ultimate Frisbee:
${context}

Question: ${question}

Provide a helpful answer about Ultimate Frisbee based on the above context:`;
    
    // Call Ollama API
    const response = await axios.post(`${OLLAMA_HOST}/api/generate`, {
      model: 'ultimate-frisbee',
      prompt: prompt,
      stream: false
    });
    
    res.json({
      answer: response.data.response
    });
  } catch (error) {
    console.error('Error calling Ollama:', error);
    res.status(500).json({ 
      error: 'Error generating response',
      answer: 'I apologize, but I was unable to generate a response at this time.'
    });
  }
});

// Add a simple health check endpoint
app.get('/health', (req, res) => {
  res.status(200).send('OK');
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Ollama API wrapper running on port ${PORT}`);
  console.log(`Connected to Ollama at ${OLLAMA_HOST}`);
});
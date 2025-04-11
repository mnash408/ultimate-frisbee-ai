const http = require('http');
const https = require('https');
const url = require('url');

// Configuration
const PORT = process.env.PORT || 8081;
const OLLAMA_HOST = process.env.OLLAMA_HOST || 'http://ollama:11434';

console.log(`Starting server on port ${PORT}...`);
console.log(`Configured to use Ollama at: ${OLLAMA_HOST}`);

// Parse JSON safely
function parseJSON(str) {
  try {
    return JSON.parse(str);
  } catch (e) {
    return null;
  }
}

// Make a request to the Ollama API
function callOllama(path, data) {
  return new Promise((resolve, reject) => {
    const ollamaUrl = new URL(OLLAMA_HOST);
    const options = {
      hostname: ollamaUrl.hostname,
      port: ollamaUrl.port,
      path: path,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    };

    const protocol = ollamaUrl.protocol === 'https:' ? https : http;
    
    const req = protocol.request(options, (res) => {
      let responseData = '';
      
      res.on('data', (chunk) => {
        responseData += chunk;
      });
      
      res.on('end', () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(parseJSON(responseData));
        } else {
          reject(new Error(`Request failed with status code ${res.statusCode}: ${responseData}`));
        }
      });
    });
    
    req.on('error', (error) => {
      reject(error);
    });
    
    req.write(JSON.stringify(data));
    req.end();
  });
}

// Create HTTP server
const server = http.createServer(async (req, res) => {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  
  // Handle preflight requests
  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }
  
  const parsedUrl = url.parse(req.url, true);
  
  // Health check endpoint
  if (parsedUrl.pathname === '/api/health' && req.method === 'GET') {
    console.log('Health check requested');
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok', ollama_host: OLLAMA_HOST }));
    return;
  }
  
  // List models
  if (parsedUrl.pathname === '/api/models' && req.method === 'GET') {
    try {
      const models = await callOllama('/api/tags', {});
      console.log('Models retrieved:', models);
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(models));
    } catch (error) {
      console.error('Error fetching models:', error.message);
      res.writeHead(500, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'Failed to fetch models', details: error.message }));
    }
    return;
  }
  
  // LLM generation endpoint
  if (parsedUrl.pathname === '/api/llm' && req.method === 'POST') {
    let body = '';
    
    req.on('data', (chunk) => {
      body += chunk.toString();
    });
    
    req.on('end', async () => {
      try {
        const data = parseJSON(body);
        
        if (!data || !data.prompt) {
          res.writeHead(400, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: 'Invalid request. Prompt is required.' }));
          return;
        }
        
        console.log(`Processing prompt: "${data.prompt.substring(0, 50)}${data.prompt.length > 50 ? '...' : ''}"`);
        
        const ollamaRequestData = {
          model: data.model || 'llama2',
          prompt: data.prompt,
          stream: false,
          options: data.options || {}
        };
        
        try {
          const response = await callOllama('/api/generate', ollamaRequestData);
          console.log('Response received from Ollama');
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ response: response.response }));
        } catch (error) {
          console.error('Error calling Ollama:', error.message);
          res.writeHead(500, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ 
            error: 'Failed to process request', 
            details: error.message 
          }));
        }
      } catch (error) {
        console.error('Error parsing request:', error.message);
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Invalid JSON', details: error.message }));
      }
    });
    
    return;
  }
  
  // Not found for all other routes
  res.writeHead(404, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: 'Not found' }));
});

// Start the server
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

// Handle termination signals
process.on('SIGINT', () => {
  console.log('Server shutting down...');
  server.close(() => {
    console.log('Server closed');
    process.exit(0);
  });
});
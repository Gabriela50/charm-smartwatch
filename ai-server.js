const http = require('http');
const path = require('path');

const miragePath = path.join(
  process.env.HOME,
  'proyectos',
  'mirage-event-planner'
);

require(path.join(miragePath, 'node_modules', 'dotenv')).config({
  path: path.join(miragePath, '.env')
});

const API_KEY = process.env.GEMINI_API_KEY;
const PORT = 3002;

function send(res, status, data) {
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*'
  });
  res.end(JSON.stringify(data));
}

const server = http.createServer(async (req, res) => {
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type'
    });
    return res.end();
  }

  if (req.method === 'GET' && req.url === '/api/health') {
    return send(res, 200, {
      status: 'ok',
      service: 'CHARM AI',
      configured: Boolean(API_KEY)
    });
  }

  if (req.method !== 'POST' || req.url !== '/api/assistant') {
    return send(res, 404, { error: 'Ruta no encontrada' });
  }

  if (!API_KEY) {
    return send(res, 500, {
      error: 'No se encontró GEMINI_API_KEY en el entorno de MIRAGE'
    });
  }

  try {
    let body = '';
    for await (const chunk of req) {
      body += chunk;
      if (body.length > 12000) {
        return send(res, 413, { error: 'La consulta es demasiado larga' });
      }
    }

    const input = JSON.parse(body || '{}');
    const prompt = String(input.prompt || '').trim();
    const language = input.language === 'en' ? 'en' : 'es';

    if (!prompt) {
      return send(res, 400, { error: 'Escribe una pregunta para CHARM' });
    }

    if (prompt.length > 2000) {
      return send(res, 400, { error: 'La pregunta supera el límite de 2000 caracteres' });
    }

    const systemInstruction = language === 'en'
      ? 'You are CHARM, a helpful and elegant smartwatch assistant. Help with agendas, calendar organization, event planning, digital passes, watch settings and general questions. Be concise and practical. Never claim that simulated suggestions are verified real events or that you have accessed live information when you have not. Answer in English.'
      : 'Eres CHARM, un asistente inteligente, cercano y elegante para un smartwatch. Ayudas con agendas, organización del calendario, planificación de eventos, entradas digitales, ajustes del reloj y preguntas generales. Responde de forma clara, útil y concisa. Nunca presentes sugerencias simuladas como eventos reales verificados ni afirmes consultar información en vivo si no tienes acceso a ella. Responde en español.';

    const response = await fetch(
      "https://" + "generativelanguage.googleapis.com/v1beta/models/" + "gemini-3.5-flash:generateContent",
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'x-goog-api-key': API_KEY
        },
        body: JSON.stringify({
          systemInstruction: {
            parts: [{ text: systemInstruction }]
          },
          contents: [{
            role: 'user',
            parts: [{ text: prompt }]
          }],
          generationConfig: {
            temperature: 0.5,
            maxOutputTokens: 700
          }
        }),
        signal: AbortSignal.timeout(25000)
      }
    );

    const result = await response.json();

    if (!response.ok) {
      console.error('Gemini respondió con HTTP', response.status);
      return send(res, 502, {
        error: 'No fue posible obtener una respuesta de CHARM IA. Inténtalo nuevamente.'
      });
    }

    const answer = (result.candidates?.[0]?.content?.parts || [])
      .map(part => part.text || '')
      .join('\n')
      .trim();

    if (!answer) {
      return send(res, 502, { error: 'CHARM IA no recibió una respuesta válida' });
    }

    return send(res, 200, { answer });
  } catch (error) {
    console.error('Error en CHARM IA:', error.message);
    return send(res, 500, {
      error: 'Ocurrió un problema al consultar CHARM IA'
    });
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`CHARM IA disponible en el puerto ${PORT}`);
  console.log(`Clave Gemini configurada: ${Boolean(API_KEY)}`);
});

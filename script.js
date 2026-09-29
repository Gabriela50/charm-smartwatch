// ==============================
// RELOJ EN TIEMPO REAL
// ==============================

function updateClock() {
    const clock = document.getElementById("clock");
  
    const now = new Date();
  
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");
  
    clock.textContent = `${hours}:${minutes}`;
  }
  
  updateClock();
  
  setInterval(updateClock, 1000);
  
  
  // ==============================
  // MODO CLARO / OSCURO
  // ==============================
  
  function toggleTheme() {
    const body = document.body;
    const themeButton = document.getElementById("themeButton");
  
    body.classList.toggle("light");
  
    if (body.classList.contains("light")) {
      themeButton.textContent = "🌙";
    } else {
      themeButton.textContent = "☀️";
    }
  }
  
  
  // ==============================
  // IA — RECOMENDACIONES
  // ==============================
  
  const recommendations = [
    "Según tus intereses, el Taller de Fotografía podría gustarte.",
    "También tienes una actividad de Diseño cerca. Comienza a las 13:30.",
    "La IA recomienda el Taller de Fotografía porque coincide con tus preferencias.",
    "Hay una actividad tranquila en la Sala Creativa. Podría ser una buena opción.",
    "Por tu historial en el evento, te recomendamos explorar el área creativa."
  ];
  
  let recommendationIndex = 0;
  
  function generateRecommendation() {
    const recommendation =
      document.getElementById("aiRecommendation");
  
    recommendation.textContent =
      recommendations[recommendationIndex];
  
    recommendationIndex++;
  
    if (recommendationIndex >= recommendations.length) {
      recommendationIndex = 0;
    }
  }
  
  
  // ==============================
  // PRÓXIMA ACTIVIDAD
  // ==============================
  
  function showNextEvent() {
    const message =
      document.getElementById("message");
  
    message.textContent =
      "⏭️ Taller de Fotografía · 12:30 · Sala Creativa";
  }
  
  
  // ==============================
  // ALERTAS
  // ==============================
  
  function showNotification() {
    const message =
      document.getElementById("message");
  
    message.textContent =
      "🔔 Tu próxima actividad comienza en 30 minutos.";
  }
  
  
  // ==============================
  // FAVORITOS
  // ==============================
  
  function showFavorite() {
    const message =
      document.getElementById("message");
  
    message.textContent =
      "♡ Taller de Fotografía guardado en favoritos.";
  }
  
  
  // ==============================
  // MAPA
  // ==============================
  
  function showLocation() {
    const message =
      document.getElementById("message");
  
    message.textContent =
      "⌖ Sala Creativa · 2 minutos desde Auditorio A.";
  }
<script>
  let apiStatus = $state('Checking...');
  let apiMessage = $state('');

  async function checkHealth() {
    try {
      const response = await fetch('/api/health');
      const data = await response.json();
      apiStatus = data.status;
      apiMessage = data.message;
    } catch (error) {
      apiStatus = 'ERROR';
      apiMessage = 'Could not connect to backend. Make sure the Spring Boot server is running on port 8080.';
    }
  }

  // Check health on mount
  $effect(() => {
    checkHealth();
  });
</script>

<main>
  <h1>💰 Budget App</h1>

  <div class="card">
    <h2>API Status</h2>
    <p class="status" class:up={apiStatus === 'UP'} class:down={apiStatus !== 'UP' && apiStatus !== 'Checking...'}>
      {apiStatus}
    </p>
    <p>{apiMessage}</p>
    <button onclick={checkHealth}>
      Refresh Status
    </button>
  </div>

  <div class="info">
    <h2>Tech Stack</h2>
    <ul>
      <li><strong>Frontend:</strong> Svelte 5 + Vite</li>
      <li><strong>Backend:</strong> Spring Boot 3.4</li>
      <li><strong>Database:</strong> H2 (in-memory for development)</li>
    </ul>
  </div>
</main>

<style>
  main {
    max-width: 800px;
    margin: 0 auto;
    padding: 2rem;
    text-align: center;
  }

  h1 {
    font-size: 2.5rem;
    color: #333;
  }

  .card {
    background: #f9f9f9;
    border-radius: 8px;
    padding: 1.5rem;
    margin: 1.5rem 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .status {
    font-size: 1.5rem;
    font-weight: bold;
    padding: 0.5rem 1rem;
    border-radius: 4px;
    display: inline-block;
  }

  .status.up {
    background: #d4edda;
    color: #155724;
  }

  .status.down {
    background: #f8d7da;
    color: #721c24;
  }

  button {
    background: #4a90d9;
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
    margin-top: 1rem;
  }

  button:hover {
    background: #357abd;
  }

  .info {
    text-align: left;
    margin-top: 2rem;
  }

  .info ul {
    list-style: none;
    padding: 0;
  }

  .info li {
    padding: 0.5rem 0;
    border-bottom: 1px solid #eee;
  }
</style>


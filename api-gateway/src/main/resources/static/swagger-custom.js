function addDevButtons() {
  function createButton(label, payload) {
    const btn = document.createElement('button');
    btn.innerText = label;
    btn.style.marginLeft = '10px';
    btn.style.padding = '6px 10px';
    btn.style.backgroundColor = '#4CAF50';
    btn.style.color = 'white';
    btn.style.border = 'none';
    btn.style.cursor = 'pointer';

    btn.onclick = async () => {
      try {
        const res = await fetch('/internal/dev-token', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-DEV-SECRET': 'my-secret'
          },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          alert("token issue failed");
          return;
        }

        const data = await res.json();
        window.ui.preauthorizeApiKey("bearerAuth", data.accessToken);
        alert(label + " login success");
      } catch (e) {
        console.error(e);
        alert("error occurred");
      }
    };

    return btn;
  }

  function addButtons() {
    const topbar = document.querySelector('.topbar');
    if (!topbar || document.getElementById('dev-login-added')) return;

    const container = document.createElement('div');
    container.id = 'dev-login-added';

    container.appendChild(createButton("ADMIN", { userId: "1", userName: "admin1", orgId: "1", role: "ADMIN" }));
    container.appendChild(createButton("USER", { userId: "2", userName: "user1", orgId: "2", role: "USER" }));
    container.appendChild(createButton("CLERGY", { userId: "3", userName: "clergy1", orgId: "2", role: "CLERGY" }));

    topbar.appendChild(container);
  }

  const interval = setInterval(() => {
    if (window.ui) {
      addButtons();
      clearInterval(interval);
    }
  }, 500);
}

if (document.readyState === 'complete') {
  addDevButtons();
} else {
  window.addEventListener('load', addDevButtons);
}

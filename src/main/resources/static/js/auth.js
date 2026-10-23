
'use strict';

function setAuthMode(mode) {
  const signup = mode === 'signup';

  window.loginPanel.hidden = signup;
  window.signupPanel.hidden = !signup;
  window.authStatus.textContent = '';
}

function showLogin(message = '') {
  currentUser = null;

  window.appScreen.hidden = true;
  window.authScreen.hidden = false;

  setAuthMode('login');
  window.authStatus.textContent = message;
}

async function checkSession() {
  try {
    const response = await fetch('/api/auth/me', {
      credentials: 'same-origin',
      headers: { 'Accept': 'application/json' }
    });

    if (!response.ok) {
      showLogin();
      return;
    }

    currentUser = await response.json();

    window.authScreen.hidden = true;
    window.appScreen.hidden = false;

    window.$('welcomeText').textContent =
      `Welcome, ${currentUser.username || 'learner'}!`;

    await Promise.allSettled([
      loadLessons(),
      loadLeaderboard(),
      loadLives(),
      loadStreak(),
      loadTotalXp()
    ]);
  } catch (error) {
    showLogin(
      'Unable to connect to LangTrack. Check that the server is running.'
    );

    console.error('Session check failed:', error);
  }
}

window.checkSession = checkSession;
window.showLogin = showLogin;

window.$('showSignup').addEventListener('click', () => {
  setAuthMode('signup');
});

window.$('showLogin').addEventListener('click', () => {
  setAuthMode('login');
});

window.$('loginForm').addEventListener('submit', async event => {
  event.preventDefault();

  window.authStatus.style.color = 'var(--muted)';
  window.authStatus.textContent = 'Logging in...';

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      credentials: 'same-origin',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        loginId: window.$('loginId').value.trim(),
        password: window.$('loginPassword').value
      })
    });

    const result = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(
        result.message ||
        result.detail ||
        result.error ||
        'Wrong username/email or password.'
      );
    }

    window.$('loginPassword').value = '';

    await checkSession();
  } catch (error) {
    window.authStatus.style.color = 'var(--red)';
    window.authStatus.textContent =
      error.message || 'Unable to log in.';
  }
});

window.$('signupForm').addEventListener('submit', async event => {
  event.preventDefault();

  window.authStatus.style.color = 'var(--muted)';
  window.authStatus.textContent = 'Creating your account...';

  try {
    const response = await fetch('/api/auth/signup', {
      method: 'POST',
      credentials: 'same-origin',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        username: window.$('signupUsername').value.trim(),
        email: window.$('signupEmail').value.trim(),
        password: window.$('signupPassword').value
      })
    });

    const result = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(
        result.message ||
        result.detail ||
        result.error ||
        'Unable to create account.'
      );
    }

    const username = window.$('signupUsername').value.trim();

    window.$('signupForm').reset();
    window.$('loginId').value = username;

    setAuthMode('login');

    window.authStatus.style.color = 'var(--green)';
    window.authStatus.textContent = 'Account created! Please log in.';
  } catch (error) {
    window.authStatus.style.color = 'var(--red)';
    window.authStatus.textContent = error.message;
  }
});

async function logout() {
  try {
    const response = await fetch('/logout', {
      method: 'POST',
      credentials: 'same-origin',
      headers: { 'Accept': 'application/json' }
    });

    if (!response.ok) {
      console.warn('The server did not confirm logout.');
    }
  } catch (error) {
    console.warn('Logout request failed.', error);
  } finally {
    window.$('modalBackdrop').hidden = true;
    showLogin('You have been logged out.');
  }
}

window.logout = logout;

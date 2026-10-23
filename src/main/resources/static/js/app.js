
'use strict';

// Shared application state. Declare currentUser only once.
let currentUser = null;
let questions = [];
let currentQuestionIndex = 0;
let selectedAnswers = [];
let answering = false;
let activeSubmodule = null;
let currentLives = 5;

// Shared DOM helper, initialized before it is used.
window.$ = function (id) {
  return document.getElementById(id);
};

// Shared elements used by authentication and the application.
window.authScreen = window.$('authScreen');
window.appScreen = window.$('appScreen');
window.authStatus = window.$('authStatus');
window.loginPanel = window.$('loginPanel');
window.signupPanel = window.$('signupPanel');

const lessonsContainer = window.$('lessonsContainer');
const learningPathStatus = window.$('learningPathStatus');
const quizCard = window.$('quizCard');
const questionArea = window.$('questionArea');
const quizStatus = window.$('quizStatus');

async function api(url, options = {}) {
  const response = await fetch(url, {
    credentials: 'same-origin',
    ...options,
    headers: {
      'Accept': 'application/json',
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(options.headers || {})
    }
  });

  if (response.status === 401) {
    throw new Error('Your session has expired. Please log in again.');
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const message = data?.message || data?.detail || data?.error;
    throw new Error(message || `Request failed (${response.status}).`);
  }

  return data;
}

function switchTab(tab) {
  window.$('homeView').hidden = tab !== 'home';
  window.$('leaderboardView').hidden = tab !== 'leaderboard';

  document.querySelectorAll('.nav-button').forEach(button => {
    button.classList.toggle('active', button.dataset.tab === tab);
  });

  closeMoreMenu();

  if (tab === 'leaderboard') {
    loadLeaderboard();
  }
}

document.querySelectorAll('.nav-button').forEach(button => {
  button.addEventListener('click', () => {
    if (button.dataset.tab === 'more') {
      toggleMoreMenu();
      return;
    }

    switchTab(button.dataset.tab);
  });
});

function toggleMoreMenu() {
  const open = window.$('moreMenu').hidden;
  window.$('moreMenu').hidden = !open;
  window.$('moreBackdrop').hidden = !open;
}

function closeMoreMenu() {
  window.$('moreMenu').hidden = true;
  window.$('moreBackdrop').hidden = true;
}

window.$('moreBackdrop').addEventListener('click', closeMoreMenu);

document.querySelectorAll('[data-more]').forEach(button => {
  button.addEventListener('click', async () => {
    const action = button.dataset.more;
    closeMoreMenu();

    if (action === 'profile') await openProfile();
    if (action === 'settings') openSettings();
    if (action === 'logout') await window.logout();
  });
});

window.$('openProfile').addEventListener('click', openProfile);

async function openProfile() {
  const modal = window.$('modalBackdrop');
  const content = window.$('modalContent');

  window.$('modalTitle').textContent = 'Your profile';

  content.innerHTML = `
    <div class="profile-grid">
      <div class="profile-item">
        <small>Username</small>
        <strong>${escapeHtml(currentUser?.username || 'Learner')}</strong>
      </div>
      <div class="profile-item">
        <small>Email</small>
        <strong>${escapeHtml(currentUser?.email || 'Not provided')}</strong>
      </div>
      <div class="profile-item">
        <small>Total XP</small>
        <strong id="profileTotalXp">${window.$('totalXp').textContent}</strong>
      </div>
      <div class="profile-item">
        <small>Current streak</small>
        <strong id="profileCurrentStreak">${window.$('currentStreak').textContent} days</strong>
      </div>
      <div class="profile-item">
        <small>Lives remaining</small>
        <strong>${window.$('livesCount').textContent} ❤️</strong>
      </div>
    </div>
    <p class="muted">Your progress is saved to your LangTrack account.</p>
  `;

  modal.hidden = false;

  await Promise.allSettled([
    loadTotalXp(),
    loadStreak()
  ]);
}

function openSettings() {
  window.$('modalTitle').textContent = 'Settings';

  window.$('modalContent').innerHTML = `
    <div class="setting-row">
      <strong>Account</strong>
      <p class="muted">Signed in as ${escapeHtml(currentUser?.username || 'learner')}.</p>
    </div>
    <div class="setting-row">
      <strong>Learning reminders</strong>
      <p class="muted">Reminder preferences can be added here later.</p>
    </div>
    <div class="setting-row">
      <strong>Privacy</strong>
      <p class="muted">Your progress is linked to your account.</p>
    </div>
    <button id="settingsLogout" class="secondary" type="button">Log out</button>
  `;

  window.$('modalBackdrop').hidden = false;

  window.$('settingsLogout').addEventListener('click', () => {
    window.logout();
  });
}

window.$('closeModal').addEventListener('click', () => {
  window.$('modalBackdrop').hidden = true;
});

window.$('modalBackdrop').addEventListener('click', event => {
  if (event.target === window.$('modalBackdrop')) {
    window.$('modalBackdrop').hidden = true;
  }
});

// Initialize session once, after all deferred scripts have executed.
document.addEventListener('DOMContentLoaded', () => {
  if (typeof window.checkSession === 'function') {
    window.checkSession();
  } else {
    console.error(
      'LangTrack: checkSession is unavailable. Check auth.js loading.'
    );
  }
});

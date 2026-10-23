async function loadLives() {
  try { const lives = Number(await api('/quiz-attempts/lives')); currentLives = Math.max(0, Math.min(5, lives)); $('livesCount').textContent = String(currentLives); $('livesCount').parentElement.setAttribute('aria-label', `${currentLives} lives remaining`); return currentLives; }
  catch (error) { console.error('Unable to load lives:', error); $('livesCount').textContent = '—'; return null; }
}
async function loadStreak() {
  if (!currentUser?.userId) return;
  try {
    const streak = await api(`/users/${encodeURIComponent(currentUser.userId)}/streak`);
    $('currentStreak').textContent = Number(streak.currentStreak ?? 0);
    if ($('profileCurrentStreak')) $('profileCurrentStreak').textContent = Number(streak.currentStreak ?? 0);
  } catch (error) { console.error('Unable to load streak:', error); }
}
async function loadTotalXp() {
  if (!currentUser?.userId) return;
  try {
    let data;
    try { data = await api(`/progress/user/${encodeURIComponent(currentUser.userId)}/summary`); }
    catch (_) { data = await api(`/progress/user/${encodeURIComponent(currentUser.userId)}`); }
    let xp = 0;
    if (typeof data === 'number') xp = data;
    else if (Array.isArray(data)) xp = data.reduce((sum, row) => sum + Number(row.xpEarned ?? row.totalXp ?? 0), 0);
    else xp = Number(data.totalXp ?? data.totalXP ?? data.xp ?? data.xpEarned ?? data.totalEarnedXp ?? 0);
    $('totalXp').textContent = String(xp);
    if ($('profileTotalXp')) $('profileTotalXp').textContent = String(xp);
  } catch (error) { console.error('Unable to load total XP. Confirm the progress summary response shape.', error); $('totalXp').textContent = '—'; }
}

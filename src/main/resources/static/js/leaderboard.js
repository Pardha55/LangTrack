async function loadLeaderboard() {
  const body = $('leaderboardBody'), status = $('leaderboardStatus'); status.textContent = 'Loading leaderboard...';
  try {
    const entries = await api('/api/leaderboard/weekly'); body.replaceChildren();
    if (!Array.isArray(entries) || !entries.length) { const row = body.insertRow(); const cell = row.insertCell(); cell.colSpan = 3; cell.textContent = 'No XP earned this week yet. Be the first to climb the leaderboard!'; status.textContent = 'Leaderboard updated.'; return; }
    entries.forEach((entry, index) => { const row = body.insertRow(); const rank = Number(entry.rank ?? index + 1); if (rank <= 3) row.className = 'top-three'; row.insertCell().textContent = rank === 1 ? '🥇 1' : rank === 2 ? '🥈 2' : rank === 3 ? '🥉 3' : String(rank); row.insertCell().textContent = entry.username ?? `User ${entry.userId ?? 'unknown'}`; row.insertCell().textContent = `${Number(entry.weeklyXp ?? 0)} XP`; });
    status.textContent = `Showing ${entries.length} learner${entries.length === 1 ? '' : 's'}.`;
  } catch (error) { body.replaceChildren(); const row = body.insertRow(); const cell = row.insertCell(); cell.colSpan = 3; cell.textContent = 'Unable to load leaderboard. Check the backend and try again.'; status.textContent = error.message; console.error(error); }
}
$('refreshLeaderboard').addEventListener('click', loadLeaderboard);


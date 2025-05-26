//sessionStorage.removeItem('loginSuccess');

document.addEventListener('DOMContentLoaded', async () => {
	await loadNavbar('navContainer');
	await loadAlerts('alertContainer');
	
	if (sessionStorage.getItem('loginError') === 'true') {
		showErrorAlert('Invalid credentials.');
		sessionStorage.removeItem('loginError');
	}
});
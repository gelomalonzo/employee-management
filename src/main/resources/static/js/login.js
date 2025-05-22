document.addEventListener('DOMContentLoaded', async () => {
	await loadNavbar('navContainer');
	await loadAlerts('alertContainer');

	const params = new URLSearchParams(window.location.search);
	if (params.has('error')) {
		console.log('Login error.');
		showErrorAlert('Invalid credentials.');
	} else if (params.has('success')) {
		showSuccessAlert('Successfully logged in. Redirecting to home page.', 2500);
	} else if (params.has('logout')) {
        showSuccessAlert('Successfully logged out.', 2500);
    }
});
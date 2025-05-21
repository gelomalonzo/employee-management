loadNavbar('navContainer');
loadAlerts('alertContainer');

const form = document.getElementById('loginForm');
form.addEventListener('submit', async function(e) {
	e.preventDefault();
	
	hideAlerts();
	
	const formData = new FormData(form);
	const params = new URLSearchParams(formData);
	
	const response = await fetch('/login', {
		method: 'POST',
		headers: {'Content-Type': 'application/x-www-form-urlencoded'},
		body: params
	});
	
	const responseData = await response.json();
	
	if (response.ok && responseData.success) {
		showSuccessAlert(responseData.message);
		setTimeout(() => { window.location.href = responseData.redirect; }, 1500);
	} else {
		showErrorAlert(responseData.message);
	}
});
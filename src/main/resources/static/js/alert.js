// Load alert.html into a container on your page
async function loadAlerts(containerId) {
	const response = await fetch('/component/alert.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;
}

// Show success alert with message
function showSuccessAlert(message, duration = 3000) {
	const alert = document.getElementById('successAlert');
	if (!alert) return;

	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');
	
	setTimeout(() => {
		alert.classList.add('d-none');
	}, duration);
}

// Show error alert with message
function showErrorAlert(message, duration = 0) {
	const alert = document.getElementById('errorAlert');
	if (!alert) return;

	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');

	setTimeout(() => {
		alert.classList.add('d-none');
	}, duration);
}

// Show warning alert with message
function showWarningAlert(message) {
	const alert = document.getElementById('warningAlert');
	if (!alert) return;
	
	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');
}

// Hide all alerts
function hideAlerts() {
	const successAlert = document.getElementById('successAlert');
	const errorAlert = document.getElementById('errorAlert');
	const warningAlert = document.getElementById('warningAlert');

	if (successAlert) successAlert.classList.add('d-none');
	if (errorAlert) errorAlert.classList.add('d-none');
	if (warningAlert) warningAlert.classList.add('d-none');
	
	loadAlerts('alertContainer');
}
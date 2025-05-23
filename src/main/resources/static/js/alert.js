async function loadAlerts(containerId) {
	const response = await fetch('/component/alert.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;
}

function showSuccessAlert(message, duration = 3000) {
	const alert = document.getElementById('successAlert');
	if (!alert) return;

	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');
	
	setTimeout(() => {
		alert.classList.add('d-none');
	}, duration);
}

async function showErrorAlert(message, duration = 3000) {
	const alert = document.getElementById('errorAlert');
	//if (!alert) return;
	console.log('ERROR ALERT');
	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');

	setTimeout(() => {
		alert.classList.add('d-none');
	}, duration);
}

function showWarningAlert(message, duration = 3000) {
	const alert = document.getElementById('warningAlert');
	if (!alert) return;
	
	alert.querySelector('.alert-message').textContent = message;
	alert.classList.remove('d-none');
	
	setTimeout(() => {
		alert.classList.add('d-none');
	}, duration);
}

function hideAlerts() {
	const successAlert = document.getElementById('successAlert');
	const errorAlert = document.getElementById('errorAlert');
	const warningAlert = document.getElementById('warningAlert');

	if (successAlert) successAlert.classList.add('d-none');
	if (errorAlert) errorAlert.classList.add('d-none');
	if (warningAlert) warningAlert.classList.add('d-none');
	
	loadAlerts('alertContainer');
}
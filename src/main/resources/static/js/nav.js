async function loadNavbar(containerId) {
	const response = await fetch('/component/nav.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;
	
	if (window.location.pathname === '/login') {
		const homeButton = document.getElementById('homeButton');
		if (homeButton) {
			homeButton.style.display = 'none';
		}
		
		const logoutButton = document.getElementById('logoutButton');
		if (logoutButton) {
			logoutButton.style.display = 'none';
		}
		
		const addEmployeeButton = document.getElementById('addEmployeeButton');
		if (addEmployeeButton) {
			addEmployeeButton.style.display = 'none';
		}
	}
}
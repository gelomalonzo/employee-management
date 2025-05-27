async function loadNavbar(containerId) {
	const response = await fetch('/component/nav.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;
	
	const path = window.location.pathname;
	const navTitle = document.getElementById('navTitle');
	if (path.includes('/home')) {
		navTitle.textContent = 'Employee Management System | Employees';
	} else if (path.includes('/departments')) {
		navTitle.textContent = 'Employee Management System | Departments';
	} else if (path.includes('/users')) {
		navTitle.textContent = 'Employee Management System | Users';
	} else {
		navTitle.textContent = 'Employee Management System';
	}
	
	try {
		const response = await fetch('/user/info');
		const responseData = await response.json();
		const role = responseData.role;
		
		const manageEmployeesButton = document.getElementById('manageEmployeesButton');
		const manageDepartmentsButton = document.getElementById('manageDepartmentsButton');
		const manageUsersButton = document.getElementById('manageUsersButton');
		const logoutButton = document.getElementById('logoutButton');
		
		if (role === 'ROLE_ADMIN') {
			// show all buttons
			manageEmployeesButton.style.display = 'inline-block';
			manageDepartmentsButton.style.display = 'inline-block';
			manageUsersButton.style.display = 'inline-block';
			logoutButton.style.display = 'inline-block';
		} else if (role === 'ROLE_USER') {
			// show Logout button
			manageEmployeesButton.style.display = 'none';
			manageDepartmentsButton.style.display = 'none';
			manageUsersButton.style.display = 'none';
			logoutButton.style.display = 'inline-block';
		} else {
			// hide all buttons
			manageEmployeesButton.style.display = 'none';
			manageDepartmentsButton.style.display = 'none';
			manageUsersButton.style.display = 'none';
			logoutButton.style.display = 'none';
		}
	} catch (error) {
		console.error('Error fetching user info: ', error);
	}
}
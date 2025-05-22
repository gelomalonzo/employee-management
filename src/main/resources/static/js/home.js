loadNavbar('navContainer');
loadAlerts('alertContainer');

window.onload = () => {
	var departments = fetchDepartments();
	var employees = fetchAllEmployees();
};

function displayHome(averageSalary, averageAge, employees) {
	const averageSalaryContainer = document.getElementById('averageSalaryContainer');
	averageSalaryContainer.innerHTML = Number(averageSalary).toLocaleString('en-US', {
		style: 'currency',
		currency: 'PHP',
		minimumFractionDigits: 2
	});
	
	const averageAgeContainer = document.getElementById('averageAgeContainer');
	averageAgeContainer.innerHTML = averageAge;
	
	const tbody = document.getElementById('employeeTableBody');
	tbody.innerHTML = '';
	employees.forEach(employee => {
		const row = document.createElement('tr');
		
		const idCell = document.createElement('td');
		idCell.className = 'text-center';
		idCell.textContent = String(employee.id).padStart(5, '0');
		row.appendChild(idCell);
		
		const nameCell = document.createElement('td');
		nameCell.className = 'text-center';
		nameCell.textContent = employee.name;
		row.appendChild(nameCell);
		
		const birthDateCell = document.createElement('td');
		birthDateCell.className = 'text-center';
		const date = new Date(employee.birthDate);
		const formattedDate = date.toLocaleDateString('en-US', {
		    year: 'numeric',
		    month: 'long',
		    day: 'numeric'
		});
		birthDateCell.textContent = formattedDate;
		row.appendChild(birthDateCell);
		
		const departmentCell = document.createElement('td');
		departmentCell.className = 'text-center';
		departmentCell.textContent = employee.department.name;
		row.appendChild(departmentCell);
		
		const salaryCell = document.createElement('td');
		salaryCell.className = 'text-center';
		salaryCell.textContent = Number(employee.salary).toLocaleString('en-US', {
			style: 'currency',
			currency: 'PHP',
			minimumFractionDigits: 2
		});
		row.appendChild(salaryCell);
		
		const actionsCell = document.createElement('td');
		const actionsCellWrapper = document.createElement('div');
		actionsCellWrapper.className = 'd-flex justify-content-center align-items-center';
		const viewButton = document.createElement('button');
		viewButton.className = 'btn btn-sm btn-outline-secondary';
		viewButton.id = 'viewButton' + employee.id;
		viewButton.textContent = 'Details';
		viewButton.onclick = () => {
            window.location.href = `/employees/${employee.id}`;
        };
		actionsCellWrapper.appendChild(viewButton);
		actionsCell.appendChild(actionsCellWrapper);
		row.appendChild(actionsCell);
		
		tbody.appendChild(row);
	});
}

async function fetchDepartments() {
	try {
		const response = await fetch('/departments/all');
		const result = await response.json();
		const departments = result.departments;
		const departmentSelect = document.getElementById('departmentSelect');
		
		departments.forEach(department => {
			const option = document.createElement('option');
			option.value = department.name;
			option.textContent = department.name;
			departmentSelect.appendChild(option);
		});
	} catch (error) {
		console.error('Error fetching departments: ', error);
	}
}

async function fetchAllEmployees() {
	try {
		const response = await fetch('/employees/all');
		const result = await response.json();
		const averageSalary = result.averageSalary;
		const averageAge = result.averageAge;
		const employees = result.employees;
		
		displayHome(averageSalary, averageAge, employees);
	} catch (error) {
		console.error('Error fetching employees: ', error);
	}
}

async function fetchEmployeeId() {
	const employeeId = document.getElementById('searchIDInput').value;
	if (employeeId === '') {
		showErrorAlert('Invalid action. Can\'t search an empty employee ID.', 3000);
        return;
    }
	
	const searchNameInput = document.getElementById('searchNameInput');
	searchNameInput.innerHTML = '';
	
	const departmentSelect = document.getElementById('departmentSelect');
	departmentSelect.innerHTML = '';
	const defaultDepartmentOption = document.createElement('option');
	defaultDepartmentOption.value = '';
	defaultDepartmentOption.textContent = 'ALL DEPARTMENTS';
	departmentSelect.appendChild(defaultDepartmentOption);
	fetchDepartments();
	
	const minAgeInput = document.getElementById('minAgeInput');
	minAgeInput.value = '';
	
	const maxAgeInput = document.getElementById('maxAgeInput');
	maxAgeInput.value = '';
	
	try {
		const response = await fetch(`/employees/${employeeId}`);
		const result = await response.json();
		const employee = result.employees;
		
		displayHome(result.averageSalary, result.averageAge, employee);
	} catch (error) {
        console.error('Error fetching employee by ID: ', error);
    }
}

async function applyFilters() {
	const searchIDInput = document.getElementById('searchIDInput');
	searchIDInput.value = '';
	
	const employeeName = document.getElementById('searchNameInput').value;
	const departmentName = document.getElementById('departmentSelect').value;
	const minAge = document.getElementById('minAgeInput').value;
	const maxAge = document.getElementById('maxAgeInput').value;
	
	if (departmentName === 'ALL DEPARTMENTS' || departmentName === '') {
		var departmentId = '';
	} else {
		try {
			const departmentResponse = await fetch(`/departments/getIdByName?departmentName=${encodeURIComponent(departmentName)}`);
			const departmentResult = await departmentResponse.json();
			
			console.log(departmentResult);
			departmentId = departmentResult.departmentId;
		} catch (error) {
		    console.error('Error fetching department ID: ', error);
		}
	}
	
	const params = new URLSearchParams({
		name: employeeName,
		departmentId: departmentId,
		minAge: minAge,
		maxAge: maxAge
	});
	
	try {
		const response = await fetch(`/employees/filter?${params.toString()}`);
		const result = await response.json();
		
		console.log(result);
		
		displayHome(result.averageSalary, result.averageAge, result.employees);
	} catch (error) {
		console.error('Error fetching filtered employee list: ', error);
	}
}

async function resetFilters() {
	const searchIDInput = document.getElementById('searchIDInput');
	searchIDInput.innerHTML = '';
	const searchNameInput = document.getElementById('searchNameInput');
	searchNameInput.innerHTML = '';
	const departmentSelect = document.getElementById('departmentSelect');
	departmentSelect.value = '';
	departmentSelect.textContent = 'ALL DEPARTMENTS';
	const minAgeInput = document.getElementById('minAgeInput');
	minAgeInput.value = '';
	const maxAgeInput = document.getElementById('maxAgeInput');
	maxAgeInput.value = '';
	
	fetchDepartments();
	fetchAllEmployees();
}
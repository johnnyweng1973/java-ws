// Fetch data from the server and populate the form
function fetchDataAndPopulateForm(id) {
	// Make a fetch request to get the data
	fetch("/memo/update-ajax/" + id)
		.then(response => response.json())
		.then(data => {
			// Populate the form fields with the fetched data
			document.getElementById('id').value = data.id;
			document.getElementById('title').value = data.title;
			document.getElementById('description').value = data.description;
			document.getElementById('category').value = data.category;
			document.getElementById('textArea').value = data.content;

			// Set the action attribute of the form
			document.getElementById('myForm').action = "/memo/update/" + id;

		})
		.catch(error => {
			console.error('Error fetching data:', error);
		});
}

document.addEventListener('DOMContentLoaded', function() {
	var memoList = document.getElementById('memoList');

	memoList.addEventListener('click', function(event) {
		var target = event.target;
		if (target && target.className == 'editButton') {
			var memoId = target.getAttribute('data-memo-id');
			// Redirect to the edit page with memoId
			fetchDataAndPopulateForm(memoId);
		}
	});

	document.getElementById('myForm').addEventListener('submit', function(event) {
		// Prevent the default form submission
		event.preventDefault();

		// Get the value of the id field
		var idValue = document.getElementById('id').value;

		// Construct the URL based on the id value
		var url = '/memo/update-ajax/' + idValue;

		// Gather form data
		var formData = new FormData(this);

		// Send AJAX request
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
		xhr.onreadystatechange = function() {
			if (xhr.readyState === XMLHttpRequest.DONE) {
				if (xhr.status === 200) {
					// Request was successful
					console.log(xhr.responseText);
					// Optionally, perform actions based on the response
				} else {
					// Request failed
					console.error('Error:', xhr.status);
				}
			}
		};
		xhr.send(formData);
	});

});




document.addEventListener('DOMContentLoaded', function() {
    var memoList = document.getElementById('memoList');

    memoList.addEventListener('click', function(event) {
        var target = event.target;
        if (target && target.classList.contains('editButton')) {
            var memoId = target.getAttribute('data-memo-id');
            // Redirect to the edit page with memoId
            window.location.href = '/memo/update/' + memoId;
        }
    });
});

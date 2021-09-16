const submitBtn = document.getElementById('submit');

submitBtn.addEventListener('click', async () => {
    console.log('test click');
    console.log(`Basic ${btoa(unescape(encodeURIComponent(document.getElementById('username').value.trim() + ':' + document.getElementById('password').value.trim())))}`);
    const resp = await fetch('/login', {
        method: 'GET',
        headers: {
            Authorization: `Basic ${btoa(unescape(encodeURIComponent(document.getElementById('username').value.trim() + ':' + document.getElementById('password').value.trim())))}`
        }
    });
    const data = await resp.json();
    console.log(data);
});
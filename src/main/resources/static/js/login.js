const submitBtn = document.getElementById('submit');

submitBtn.addEventListener('click', async () => {
  const resp = await fetch('/authorize', {
    method: 'GET',
    headers: {
      Authorization: `Basic ${btoa(unescape(encodeURIComponent(document.getElementById('username').value.trim() + ':' + document.getElementById('password').value.trim())))}`
    }
  });
  const session = await resp.text();
  console.log(session);
});

const showSignupBtn = document.getElementById('show-signup-button');

showSignupBtn.href = "javascript:void(0)";

const signupForm = document.getElementById('signup-container');

showSignupBtn.addEventListener('click', async () => {
  if (signupForm.getBoundingClientRect().width === 0) {
    signupForm.style.display = 'block';
  } else {
    signupForm.style.display = 'none';
  }
});

const signupBtn = document.getElementById('signup');

signupBtn.addEventListener('click', async () => {
  const username = document.getElementById('signup-username').value;
  const password = document.getElementById('signup-password').value;
  const passwordReenter = document.getElementById('signup-password-reenter').value;
  for (const val of [username, password, passwordReenter]) {
    if (val.length === 0) {
      // подсказка
      return;
    }
  }
  if (password !== passwordReenter) {
    // подсказка
    return;
  }

  const response = await fetch('/signup', {
    method: 'POST',
    headers: {
      'content-type': 'application/json'
    },
    body: JSON.stringify({
      username,
      password
    })
  });

  password.value = passwordReenter.value = '';
});
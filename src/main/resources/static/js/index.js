async function testFetch() {
  await fetch('/go/state', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      game_id: "1"
    })
  });
  
  const res = await fetch('/go/act', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      game_id: "321",
      action: {
        type: 'put_stone',
        params: {
          point: [3, 5]
        }
      }
    })
  });
}

// testFetch();

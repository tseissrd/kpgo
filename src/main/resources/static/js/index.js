async function testFetch() {
  console.log(1);
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
  console.log(2);
}

testFetch();

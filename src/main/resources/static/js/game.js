const gameTableDock = document.getElementById('go-container');
const gameId = 1;
updateGameTable(1);

function constructGameTable(tableObject) {
  const tableEl = document.createElement('table');
  tableEl.className = 'go-game-table';
  for (let rowNumber = 0; rowNumber < tableObject.length; rowNumber += 1) {
    const row = tableObject[rowNumber];
    const rowEl = document.createElement('tr');
    rowEl.className = 'go-game-table-row';
    tableEl.appendChild(rowEl);
    for (let columnNumber = 0; columnNumber < row.length; columnNumber += 1) {
      const point = row[columnNumber];
      
      const tablePointEl = document.createElement('td');
      tablePointEl.className = 'go-game-table-point';
      rowEl.appendChild(tablePointEl);
      
      const stoneContainerEl = document.createElement('div');
      stoneContainerEl.className = 'go-game-stone-container';
      tablePointEl.appendChild(stoneContainerEl);
      
      if (point !== 'EMPTY') {
        const stoneImgEl = document.createElement('img');
        stoneImgEl.className = 'go-game-stone';
        if (point === 'BLACK')
          stoneImgEl.src = '/img/black-stone.png';
        else if (point === 'WHITE')
          stoneImgEl.src = '/img/white-stone.png';
        stoneContainerEl.appendChild(stoneImgEl);
      } else {
        tablePointEl.addEventListener('click', async () => {
          await act('place_stone', {point: [columnNumber, rowNumber]});
          await updateGameTable();
        });
      }
    }
  }
  
  return tableEl;
}

async function updateGameTable() {
  const state = await getState();
  const tableObject = state.table;
  while (gameTableDock.childNodes.length > 0)
    gameTableDock.removeChild(gameTableDock.firstChild);
  
  const table = constructGameTable(tableObject);
  gameTableDock.appendChild(table);
}

async function getState() {
  const res = await fetch('/go/state', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      game_id: gameId
    })
  });
  
  return await res.json();
}
 
async function act(action, params) { 
  const res = await fetch('/go/act', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      game_id: gameId,
      action: {
        type: action,
//        params: {
//          point: [3, 5]
//        }
        params
      }
    })
  });
  
  return await res.json();
}

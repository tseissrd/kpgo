let gameId;
const gameTableDock = document.getElementById('go-container');
let actsNext = false;
startGame();

async function startGame() {
  let gameQuery = {status: false};
  while (gameQuery.status === false) {
    gameQuery = await queryGame();
  }
  gameId = gameQuery.game_id;
  play();
}

async function play() {
  updateGameTable();

  const heartbeatTimer = setInterval(heartbeat, 2000);
}

async function queryGame() {
  const res = await fetch('/queue', {
    method: 'GET'
  });
  
  return await res.json();
}

async function heartbeat() {
  if (!actsNext)
    updateGameTable();
}

function constructBackground(width, height) {
  const backgroundEl = document.createElement('div');
  backgroundEl.className = 'go-game-table-background';
  backgroundEl.style.width = width;
  backgroundEl.style.height = height;
  return backgroundEl;
}

function constructGrid(tableObject) {
  const tableBackgroundEl = document.createElement('table');
  tableBackgroundEl.className = 'go-game-table-grid';
  for (let rowNumber = 0; rowNumber < tableObject.length - 1; rowNumber += 1) {
    const row = tableObject[rowNumber];
    const rowEl = document.createElement('tr');
    rowEl.className = 'go-game-table-row-grid';
    tableBackgroundEl.appendChild(rowEl);
    for (let columnNumber = 0; columnNumber < row.length - 1; columnNumber += 1) {
      const tablePointEl = document.createElement('td');
      tablePointEl.className = 'go-game-table-grid-point';
      rowEl.appendChild(tablePointEl);
    }
  }
  
  return tableBackgroundEl;
}

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
  actsNext = state.act;
  const tableObject = state.table;
  while (gameTableDock.childNodes.length > 0)
    gameTableDock.removeChild(gameTableDock.firstChild);
  
  const table = constructGameTable(tableObject);
  gameTableDock.appendChild(table);
  const grid = constructGrid(tableObject);
  gameTableDock.appendChild(grid);
  const tableRect = table.getBoundingClientRect();
  const background = constructBackground(tableRect.width, tableRect.height);
  gameTableDock.appendChild(background);
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
  if (actsNext) {
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
}

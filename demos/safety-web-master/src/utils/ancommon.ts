export function getMenusCode(data?: Array<Object>) {
  let res = [];
  data &&
    data.forEach((item) => {
      const arr: Array<String> = item.code ? item.code.split(':') : [];
      res = [...res, ...arr, item.code];
      if (arr.length > 2) {
        res.push(`${arr[0]}:${arr[1]}`);
      }
      // if (item.code) {
      //   res.push(item.code);
      // }
      // res = [...getMenusCode(item.children), ...res];
    });
  return res;
}

const filterMenu = (data, authList) => {
  const filtered = data.filter((item:any) => {
    return [...authList, 'home', '*'].indexOf(item.code) > -1;
  });
  filtered.forEach(item => {
    if (item.children) {
      item.children = filterMenu(item.children, authList)
    }
  })
  
  return filtered
}
export function getMenusAuth(data: Array<Object>, authList: Array<any>) {
  const res = filterMenu(data, authList)
  // let res = data.filter((item:any) => {
  //   return [...authList, 'home', '*'].indexOf(item.code) > -1;
  // });
  // res = res.map((item:any) => {
  //   return {
  //     ...item,
  //     children:
  //       item.children &&
  //       item.children.filter((i) => {
  //         const child = [...authList, 'home', '*'].indexOf(i.code) > -1;
  //         return child;
  //       }),
  //   };
  // });
  console.log(res, 'resssss');
  
  return res;
}

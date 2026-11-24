import axios from 'axios';
const service = axios.create({
  // baseURL: URL_BASE // api的base_url
  // timeout: 30000, // 请求超时时间
});
service.interceptors.response.use(
  (response) => {
    // Do something with response data
    return response;
  },
  (error) => {
    // Do something with response error
    // console.log(error, '5555555555555555');
    // const errorcode = error.response && error.response.status;
    // if (errorcode === '404') {
    //   alert(error);
    // } else if (!errorcode) {
    //   alert('请求超时或网络异常');
    // } else {
    //   alert('请求失败');
    // }
    return Promise.reject(error);
  }
);
const idCard = 'http://127.0.0.1:18889/api/readCard?ReadSN=1';
const ICCard = 'http://127.0.0.1:18889/api/readIDCardSN';
function doAjax($cfg) {
  return service($cfg)
    .then((res) => {
      if (res.status === 401) {
        // alert(res.msg);
      } else if (res.data.status === 403) {
        // alert("没有权限");
      } else {
        return res.data;
      }
    })
    .catch((error) => {
      const res = error.response;
      return res;
    });
}

export { doAjax, idCard, ICCard };

import { Modal } from "antd";
export { errHandler };

const errHandler = (error) => {
  Modal.error({
    title: "errorCode: " + error.response.data.errorCode,
    content: error.response.data.errorMessage,
  });
  console.log("error", error);
};

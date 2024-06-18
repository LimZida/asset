import { Modal } from "antd";

const errHandler = (error) => {
  const errorCode = error.response?.data?.errorCode ?? "Unknown error code";
  const errorMessage = error.response?.data?.errorMessage ?? "An unknown error occurred";

  Modal.error({
    title: "errorCode: " + errorCode,
    content: errorMessage,
  });

  console.log("error", error);
};

export { errHandler };

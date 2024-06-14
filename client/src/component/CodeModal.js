import React, { useState, useEffect } from "react";
import { Button, Input, Modal, Space, Descriptions, Spin } from "antd";
import { LoadingOutlined, CheckOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";

const CategoryModal = (props) => {
  const [code, setCode] = useState("");
  const [codeName, setCodeName] = useState("");
  const [codeRemark, setCodeRemark] = useState("");
  const [validateDept, setValidateDept] = useState(false);
  const [loading, setLoading] = useState(false);
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

  const initData = () => {
    setValidateDept(false);
    setCode("");
    setCodeName("");
    setCodeRemark("");
  };

  const handleOk = () => {
    if (!validateDept) {
      Modal.error({
        title: "코드명 중복확인이 필요합니다.",
      });
      return;
    }
    props.closeCodeModal(false);
  };

  const handleCancel = () => {
    initData();
    props.closeCodeModal(false);
  };

  /**
   * @title 카테고리 중복검사
   */
  const reqValidations = async () => {
    if (!codeName) {
      Modal.error({
        title: "코드명을 입력해주세요.",
      });
      return;
    }
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/validations";
    setLoading(true);

    try {
      const res = await request.post(url, {
        codeName: codeName,
        codeType: props.selected.codeType,
      });

      if (res.data.result === "dup") {
        Modal.error({
          content: "중복된 코드타입입니다.",
        });
      } else if (res.data.result === "notDup") {
        Modal.success({
          title: "사용가능한 코드타입입니다.",
        });
        setValidateDept(true);
        setCode(res.data.code);
        setLoading(false);
      } else {
        console.log("res", res);
      }
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  /**
   * @title 카테고리 추가
   */
  const createCode = () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    request
      .post(url, {
        codeCtg: props.selected.value,
        code: code,
        codeName: codeName,
        codeType: props.selected.codeType,
      })
      .then((res) => {
        props.updateCategory();
        Modal.success({
          title: "코드가 추가되었습니다.",
        });
        setLoading(false);
      })
      .catch((error) => {
        //실패
        Modal.error({
          title: "errorCode: " + error.response.data.errorCode,
          content: error.response.data.errorMessage,
        });
        console.log(error);
        setLoading(false);
      });
  };

  return (
    <Modal
      title="코드 추가"
      open={props.showModal}
      onOk={handleOk}
      onCancel={handleCancel}
      okButtonProps={{
        children: "Custom OK",
      }}
      cancelButtonProps={{
        children: "Custom cancel",
      }}
      okText="저장"
      cancelText="닫기"
      loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
    >
      <Space wrap>
        <Descriptions column={1} bordered>
          <Descriptions.Item label="코드값">
            <Input style={{ width: 300 }} value={code} disabled />
          </Descriptions.Item>
          <Descriptions.Item label="코드명">
            <div>
              <Input
                style={{ width: 160 }}
                value={codeName}
                onChange={(event) => {
                  setCodeName(event.target.value);
                }}
              />
              <Button style={{ width: 130, marginLeft: 10 }} className="primaryBtn" onClick={reqValidations}>
                중복확인
                {validateDept && (
                  <span style={{ marginLeft: 10 }}>
                    <CheckOutlined />
                  </span>
                )}
              </Button>
            </div>
          </Descriptions.Item>
          <Descriptions.Item label="비고">
            <Input style={{ width: 300 }} value={codeRemark} onChange={(e) => setCodeRemark(e.target.value)} />
          </Descriptions.Item>
        </Descriptions>
      </Space>
    </Modal>
  );
};

export default CategoryModal;

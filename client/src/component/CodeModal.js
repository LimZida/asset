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

  useEffect(() => {
    setValidateDept(false);
  }, [codeName]);

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
    createCode();
  };

  const handleCancel = () => {
    initData();
    props.closeCodeModal(false);
  };

  /**
   * @title 코드 중복검사
   */
  const reqValidations = async () => {
    if (!codeName) {
      Modal.error({
        title: "코드명을 입력해주세요.",
      });
      return;
    }
    let url = "http://218.55.79.57:8087/mcnc-mgmts/code-managements/validations";
    setLoading(true);

    try {
      const res = await request.post(url, {
        codeCtg: props.selected.value,
        codeName: codeName,
        codeType: props.selected.codeInnerType.replace(/[()]/g, ""),
      });
      if (res.data.code) {
        Modal.success({
          title: "사용가능한 코드명입니다.",
        });
        setValidateDept(true);
      } else {
        Modal.error({
          content: "중복된 코드명입니다.",
        });
        setValidateDept(false);
      }
      setCode(res.data.code);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 코드 추가
   */
  const createCode = async () => {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/code-managements";
    setLoading(true);

    try {
      await request.post(url, {
        codeCtg: props.selected.value,
        code: code,
        codeName: codeName,
        codeType: props.selected.codeInnerType.replace(/[()]/g, ""),
        codeRemark: codeRemark,
      });

      props.updateCategory();
      Modal.success({
        title: "코드가 추가되었습니다.",
      });
      props.updateCode(props.selected.value);
      props.closeCodeModal(false);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
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

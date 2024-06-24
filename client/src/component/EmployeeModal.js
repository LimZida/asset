import React, { useState, useEffect } from "react";
import { Button, Input, Modal, Space, Descriptions, Spin, Select } from "antd";
import { LoadingOutlined, CheckOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";

const CategoryModal = (props) => {
  const [userName, setUserName] = useState("");
  const [userId, setUserId] = useState("");
  const [deptValue, setDeptValue] = useState(""); // 추가된 상태
  const [validateDept, setValidateDept] = useState(false);
  const [loading, setLoading] = useState(false);
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

  useEffect(() => {
    setValidateDept(false);
  }, [userId]);

  const initData = () => {
    setValidateDept(false);
    setUserName("");
    setUserId("");
  };

  const handleOk = () => {
    if (!validateDept) {
      Modal.error({
        title: "코드명 중복확인이 필요합니다.",
      });
      // props.closeCodeModal(false);
      return;
    }
    createCode();
  };

  const handleCancel = () => {
    initData();
    props.closeCodeModal(false);
  };

  /**
   * @title 아이디 중복검사
   */
  const reqValidations = async () => {
    if (!userId) {
      Modal.error({
        title: "아이디를 입력해주세요.",
      });
      return;
    }
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees/validations";
    setLoading(true);

    try {
      const res = await request.post(url, {
        userId: userId,
      });

      if (res.data.userSelectResult === "dup") {
        Modal.error({
          content: "중복된 아이디입니다.",
        });
      } else if (res.data.userSelectResult === "notDup") {
        Modal.success({
          title: "사용가능한 아이디입니다.",
        });
        setValidateDept(true);
      }
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 임직원 추가 수정중
   */
  const createCode = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees";
    setLoading(true);
    if (!userName) {
      Modal.error({
        title: "이름을 입력해주세요.",
      });
      return;
    }
    if (!deptValue) {
      Modal.error({
        title: "부서를 선택해주세요.",
      });
      return;
    }

    try {
      await request.post(url, {
        userId: userId,
        userDept: deptValue,
        userName: userName,
      });

      props.updateEmployee();
      Modal.success({
        title: "직원이 추가되었습니다.",
      });
      props.closeCodeModal(false);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="직원 추가"
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
          <Descriptions.Item label="이름">
            <Input style={{ width: 300 }} value={userName} onChange={(e) => setUserName(e.target.value)} />
          </Descriptions.Item>
          <Descriptions.Item label="아이디">
            <div>
              <Input
                style={{ width: 160 }}
                value={userId}
                onChange={(event) => {
                  setUserId(event.target.value);
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
          <Descriptions.Item label="부서">
            <Select style={{ width: 300 }} options={props.deptList} value={deptValue} onChange={(value) => setDeptValue(value)} />
          </Descriptions.Item>
        </Descriptions>
      </Space>
    </Modal>
  );
};

export default CategoryModal;

import React, { useState, useEffect } from "react";
import { LoadingOutlined, CheckOutlined } from "@ant-design/icons";
import { Button, Input, Typography, Modal, Select, Space, Descriptions, Spin, Alert } from "antd";
import request from "../instance";

const { Text } = Typography;

const CategoryModal = (props) => {
  const handleOk = () => {
    if (!validateDept1) {
      Modal.error({
        title: "카테고리명 중복확인이 필요합니다.",
      });
    } else {
      categorysAdd();
      props.closeCateModal(false);
    }
  };

  const handleCancel = () => {
    props.closeCateModal(false);
  };

  const [categoryEditFlag, setCategoryEditFlag] = useState(false);
  const [codeDepth2Data, setCodeDepth2Data] = useState([]);
  const [categoryCode, setCategoryCode] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [codeType, setCodeType] = useState("");
  const [errText1, setErrText1] = useState(false);
  const [errText2, setErrText2] = useState(false);
  const [errText3, setErrText3] = useState(false);
  const [selectedDept1, setSelectedDept1] = useState({ value: "categoryAdd", upperCode: "CTG01" });
  const [selectedDept2, setSelectedDept2] = useState({});
  const [showCodeType, setShowCodeType] = useState(false);
  const [loading, setLoading] = useState(false);
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [validateDept1, setValidateDept1] = useState(false);

  /** 카테고리 관리 셀렉트 이벤트 */
  const handleDept1 = (value, e) => {
    setShowCodeType(false);
    // 카테고리 추가
    if (value === "categoryAdd") {
      setCategoryEditFlag(false);
      setCategoryCode("");
      setCategoryName("");
      setSelectedDept1({ value: "categoryAdd", upperCode: "CTG01" });
      setCodeDepth2Data([]);
    } else {
      setCategoryEditFlag(true);
      setCategoryCode(value);
      setCategoryName(e.codeName);
      setSelectedDept1(e);
      if (e.children) {
        let temp = [...e.children];
        temp.unshift({ value: "categoryEdit", label: "카테고리 수정" }, { value: "categoryAdd", label: "카테고리 추가" });
        setCodeDepth2Data(temp);
      }
    }
  };

  const handleDept2 = (value, e) => {
    setSelectedDept2(e);
    if (value === "categoryEdit") {
      setCategoryName(selectedDept1.codeName);
      setCategoryCode(selectedDept1.code);
    } else {
      if (value === "categoryAdd") {
        setCategoryCode("");
        setCategoryName("");
      } else {
        setCategoryCode(value);
        setCategoryName(e.codeName);
      }
    }
  };

  let codeDepth1data = props.originMenuData.filter((item) => {
    if ([1].includes(item.codeDepth)) {
      item.label = item.codeName;
      item.value = item.code;
      return true;
    }
    return false;
  });
  codeDepth1data.unshift({ value: "categoryAdd", label: "카테고리 추가" });

  useEffect(() => {
    setValidateDept1(false);
    if (categoryName === "") {
      setErrText1(true);
    } else {
      setErrText1(false);
    }
  }, [categoryName]);

  useEffect(() => {
    if (codeType === "") {
      setErrText2(true);
      setErrText3(false);
    } else {
      setErrText2(false);
      const regex = /^[A-Z]{2,3}$/;
      if (regex.test(codeType)) {
        setErrText3(false);
      } else {
        setErrText3(true);
      }
    }
  }, [codeType]);

  useEffect(() => {
    if (selectedDept2.value === "categoryAdd") {
      setShowCodeType(true);
    } else {
      setShowCodeType(false);
    }
  }, [selectedDept2]);

  /**
   * @title 카테고리 중복검사
   */
  const reqValidations = () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys/validations";
    setLoading(true);

    request
      .post(url, {
        codeName: categoryName,
        codeType: "CTG",
        upperCode: selectedDept1.upperCode,
      })
      .then((res) => {
        setValidateDept1(true);
        setCategoryCode(res.data.code);
        setLoading(false);
      })
      .catch((error) => {
        //실패
        console.log(error);
        console.log("실패");
        setLoading(false);
      });
  };

  /**
   * @title 카테고리 추가
   */
  const categorysAdd = () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    request
      .post(url, {
        upperCode: selectedDept1.upperCode,
        codeDepth: "1",
        codeName: categoryName,
        code: categoryCode,
        codeType: "CTG",
      })
      .then((res) => {
        props.updateCategory();
        setLoading(false);
      })
      .catch((error) => {
        //실패
        console.log(error);
        console.log("실패");
        setLoading(false);
      });
  };

  return (
    <Modal
      title="카테고리 관리"
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
          <Descriptions.Item label="Dept1">
            <Select defaultValue="카테고리 추가" style={{ width: 300 }} onChange={handleDept1} options={codeDepth1data} />
          </Descriptions.Item>
          {categoryEditFlag && (
            <Descriptions.Item label="Dept2">
              <Select defaultValue="카테고리 수정" style={{ width: 300 }} onChange={handleDept2} options={codeDepth2Data} />
            </Descriptions.Item>
          )}
          <Descriptions.Item label="카테고리값">
            <Input style={{ width: 300 }} value={categoryCode} disabled />
          </Descriptions.Item>
          <Descriptions.Item label="카테고리명">
            <div>
              <Input
                style={{ width: 160 }}
                value={categoryName}
                onChange={(event) => {
                  setCategoryName(event.target.value);
                }}
              />
              {(selectedDept1.value === "categoryAdd" || selectedDept2.value === "categoryAdd") && (
                <>
                  <Button style={{ width: 130, marginLeft: 10 }} className="primaryBtn" onClick={reqValidations}>
                    중복확인
                    {validateDept1 && (
                      <span style={{ marginLeft: 10 }}>
                        <CheckOutlined />
                      </span>
                    )}
                  </Button>
                </>
              )}
            </div>
            {errText1 && <Text type="danger">카테고리명은 필수 입력사항입니다.</Text>}
          </Descriptions.Item>
          {showCodeType && (
            <Descriptions.Item label="코드타입(ex: DPT, OSY, SYS)">
              <div>
                <Input
                  style={{ width: 160 }}
                  value={codeType}
                  onChange={(event) => {
                    setCodeType(event.target.value);
                  }}
                />
                <Button style={{ width: 130, marginLeft: 10 }} className="primaryBtn">
                  중복확인
                  <span style={{ marginLeft: 10 }}>
                    <CheckOutlined />
                  </span>
                </Button>
              </div>
              {errText2 && <Text type="danger">코드타입은 필수 입력사항입니다.</Text>}
              {errText3 && <Text type="danger">코드타입은 대문자 영어 2~3자리여야 합니다.</Text>}
            </Descriptions.Item>
          )}
        </Descriptions>
      </Space>
    </Modal>
  );
};

export default CategoryModal;

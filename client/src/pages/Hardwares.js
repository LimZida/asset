import "../styles/Hardwares.css";
import axios from "axios";
import React, { useState, useEffect } from "react";
import { Button, Input, Space, Table, Spin } from "antd";
import { RedoOutlined, LoadingOutlined } from "@ant-design/icons";
import request from "../instance";
import { render } from "@testing-library/react";
import HardwareModal from '../component/HardwareModal'

const { Search } = Input;

const columns = [
  {
    title: "자산번호",
    dataIndex: "hwNo",
  },
  {
    title: "자산구분",
    dataIndex: "hwDiv",
    render: (hwDiv) => `${hwDiv.codeName}`,
  },
  {
    title: "위치",
    dataIndex: "hwLocation",
    render: (hwLocation) => `${hwLocation.codeName}`,
  },
  {
    title: "제조사",
    dataIndex: "hwMfr",
    render: (hwMfr) => `${hwMfr.codeName}`,
  },
  {
    title: "모델명",
    dataIndex: "hwModel",
  },
  {
    title: "부서",
    dataIndex: "usageInfo",
    render: (usageInfo) => `${usageInfo.usageDept.codeName}`,
  },
  {
    title: "현사용자",
    dataIndex: "usageInfo",
    render: (usageInfo) => `${usageInfo.usageName}`,
  },
  {
    title: "자산상태",
    dataIndex: "usageInfo",
    render: (usageInfo) => `${usageInfo.assetStatus.codeName}`,
  },
  {
    title: "최종변경일",
    dataIndex: "updateDate",
  },
];

const Hardwares = () => {
  const [data, setData] = useState();
  const [searchList, setSearchList] = useState();
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState({});
  // const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

  const onSearch = (value) => {
    var list = data.filter((list) => list.hwNo.indexOf(value) > -1 || list.hwDiv.codeName.indexOf(value) > -1 || list.hwLocation.codeName.indexOf(value) > -1 || list.hwMfr.codeName.indexOf(value) > -1 || list.hwModel.indexOf(value) > -1 || list.usageInfo.usageDept.codeName.indexOf(value) > -1 || (list.usageInfo.usageName || "").indexOf(value) > -1 || (list.usageInfo.assetStatus.codeName || "").indexOf(value) > -1 || list.updateDate.indexOf(value) > -1);

    var searchData = [];
    for (var i = 0; i < list.length; i++) {
      searchData.push({ key: i, ...list[i] });
    }
    console.log(searchData);
    setSearchList(list);
  };

  useEffect(() => {
    reqHardList();
  }, []);

  //하드웨어 목록 조회
  const reqHardList = function () {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/assets/hardwares";
    setLoading(true);

    request
      .get(url)
      .then((res) => {
        setData(res.data);
        var searchData = [];
        for (var i = 0; i < res.data.length; i++) {
          searchData.push({ key: i, ...res.data[i] });
        }
        setSearchList(searchData);
        setLoading(false);
      })
      .catch((res) => {
        //실패
        console.log("실패");
        setLoading(false);
      });
  };

  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const onSelectChange = (newSelectedRowKeys) => {
    console.log("selectedRowKeys changed: ", newSelectedRowKeys);
    setSelectedRowKeys(newSelectedRowKeys);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
    selections: [
      Table.SELECTION_ALL,
      Table.SELECTION_INVERT,
      Table.SELECTION_NONE,
      // {
      //   key: 'odd',
      //   text: 'Select Odd Row',
      //   onSelect: (changeableRowKeys) => {
      //     let newSelectedRowKeys = [];
      //     newSelectedRowKeys = changeableRowKeys.filter((_, index) => {
      //       if (index % 2 !== 0) {
      //         return false;
      //       }
      //       return true;
      //     });
      //     setSelectedRowKeys(newSelectedRowKeys);
      //   },
      // },
      // {
      //   key: 'even',
      //   text: 'Select Even Row',
      //   onSelect: (changeableRowKeys) => {
      //     let newSelectedRowKeys = [];
      //     newSelectedRowKeys = changeableRowKeys.filter((_, index) => {
      //       if (index % 2 !== 0) {
      //         return true;
      //       }
      //       return false;
      //     });
      //     setSelectedRowKeys(newSelectedRowKeys);
      //   },
      // },
    ],
  };
  return (
    <Space direction="vertical">
      <div style={{ float: "right" }}>
        <Search
          placeholder="검색"
          onSearch={onSearch}
          style={{
            width: 200,
            marginRight: 10,
          }}
        />
        <Button
          type="primary"
          shape="circle"
          icon={<RedoOutlined />}
          onClick={() => {
            reqHardList();
          }}
        />
      </div>
      <Table
        loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
        rowSelection={rowSelection}
        columns={columns}
        dataSource={searchList}
        onHeaderRow={(record, idx) => {
          console.log(record);
          console.log(idx);
        }}
        onRow={(record, rowIndex) => {
          return {
            onClick: (event) => {
              setSelectedRecord(record);
               setShowModal(true);
            }, // click row
          };
        }}
      />
      <HardwareModal showModal={showModal} selected={selectedRecord} closeCodeModal={false} updateCategory={''} updateCode={''} />
    </Space>
  );
};

export default Hardwares;

import React, { useState, useEffect } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";
import { Input, Table, Spin, DatePicker, Layout, theme, Select, Button, Space, Descriptions } from "antd";
import dayjs from "dayjs";
const { Content } = Layout;
const { RangePicker } = DatePicker;

const History = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [loading, setLoading] = useState(false);
  const [searchList, setSearchList] = useState();
  const [searchValue, setSearchValue] = useState("");
  const [dateSetting, setDateSetting] = useState([dayjs(), dayjs().subtract(1, "year")]);
  const [hwList, setHwList] = useState([]);
  const [swList, setSwList] = useState([]);
  const [allList, setAllList] = useState([]);
  const [assetType, setAssetType] = useState([]);
  const [selectedFilter1, setSelectedFilter1] = useState("ALL");
  const [selectedFilter2, setSelectedFilter2] = useState("ALL");
  const [selectedFilter3, setSelectedFilter3] = useState("ALL");

  const assetList = [
    {
      label: "선택",
      value: "ALL",
    },
    {
      label: "자산번호",
      value: "assetNo",
    },
    {
      label: "자산구분",
      value: "assetCategory",
    },
    {
      label: "자산명",
      value: "modelName",
    },
    {
      label: "이전 사용자",
      value: "previousUserName",
    },
    {
      label: "이후 사용자",
      value: "newUserName",
    },
    {
      label: "자산상태",
      value: "assetStatus",
    },
    {
      label: "최종변경일",
      value: "createDate",
    },
  ];

  useEffect(() => {
    reqHistoryData();
    reqAnalyticsData();
  }, []);

  /**
   * @title 검색필터 조회
   */
  const reqAnalyticsData = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/history/analytics";
    setLoading(true);
    try {
      const res = await request.get(url);

      let hwList = res.data.hwList.filter((item) => {
        item.label = item.codeName;
        item.value = item.code;
        return true;
      });
      let swList = res.data.swList.filter((item) => {
        item.label = item.codeName;
        item.value = item.code;
        return true;
      });
      hwList.unshift({ value: "ALL", label: "전체" });
      setAllList([...hwList, ...swList]);
      setHwList(hwList);
      swList.unshift({ value: "ALL", label: "전체" });
      setSwList(swList);
      let assetFlag = [
        { value: "ALL", label: "전체" },
        { value: "hwList", label: "하드웨어" },
        { value: "swList", label: "소프트웨어" },
      ];
      setAssetType(assetFlag);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 히스토리 조회
   */
  const reqHistoryData = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/history/history-total";
    setLoading(true);
    const startDate = dateSetting[1].subtract(1, "year").format("YYYYMMDD") + "000000";
    const endDate = dateSetting[0].format("YYYYMMDD") + "235959";

    try {
      const initialResponse = await request.post(url, {
        assetHistoryList: {
          assetFlag: "ALL",
          assetCategory: "ALL",
          historyCategory: "ALL",
          endDate: endDate,
          pageCntLimit: 1,
          keyword: "",
          startDate: startDate,
          startIdx: 0,
        },
      });

      const totalCnt = initialResponse.data.assignHistoryTotalCnt;

      const res = await request.post(url, {
        assetHistoryList: {
          assetFlag: selectedFilter1,
          assetCategory: selectedFilter2,
          historyCategory: selectedFilter3,
          endDate: endDate,
          pageCntLimit: totalCnt,
          keyword: searchValue,
          startDate: startDate,
          startIdx: 0,
        },
      });
      const searchData = res.data.assetHistoryList.map((item, index) => ({
        key: index,
        ...item,
      }));
      setSearchList([...searchData]);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  const onChange = (date, dateString) => {
    setDateSetting([...date]);
  };

  const onChangeAsset1 = (value, e) => {
    setSelectedFilter1(e.value);
  };

  const onChangeAsset2 = (value, e) => {
    setSelectedFilter2(e.value);
  };

  const onChangeAsset3 = (value, e) => {
    setSelectedFilter3(e.value);
  };

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const columns = [
    {
      title: "자산번호",
      dataIndex: "assetNo",
      key: "assetNo",

      sorter: (a, b) => {
        return a.assetNo.localeCompare(b.assetNo);
      },
    },
    {
      title: "자산구분",
      dataIndex: "assetCategory",
      key: "assetCategory",
      render: (assetCategory, record) => `${record.assetFlag}-${record.assetCategory}`,
      sorter: (a, b) => {
        return a.assetCategory.localeCompare(b.assetCategory);
      },
    },
    {
      title: "자산명",
      dataIndex: "modelName",
      key: "modelName",
      sorter: (a, b) => {
        return a.modelName.localeCompare(b.modelName);
      },
    },
    {
      title: "이전 사용자",
      dataIndex: "previousUserName",
      key: "previousUserName",
      render: (previousUserName, record) => `${record.previousUserName}(${record.previousUserDept})`,
      sorter: (a, b) => {
        return a.previousUserName.localeCompare(b.previousUserName);
      },
    },
    {
      title: "이후 사용자",
      dataIndex: "newUserName",
      key: "newUserName",
      render: (newUserName, record) => `${record.newUserName}(${record.newUserDept})`,
      sorter: (a, b) => {
        return a.newUserName.localeCompare(b.newUserName);
      },
    },
    {
      title: "자산상태",
      dataIndex: "assetStatus",
      key: "assetStatus",
      sorter: (a, b) => {
        return a.assetStatus.localeCompare(b.assetStatus);
      },
    },
    {
      title: "최종변경일",
      dataIndex: "createDate",
      key: "createDate",
      sorter: (a, b) => {
        return a.createDate.localeCompare(b.createDate);
      },
    },
  ];

  return (
    <Content
      style={{
        padding: 24,
        margin: 0,
        minHeight: 280,
        background: colorBgContainer,
        borderRadius: borderRadiusLG,
      }}
    >
      <Space wrap style={{ marginBottom: 20, width: "100%" }}>
        <Descriptions bordered style={{ width: "100%" }}>
          <Descriptions.Item label="대분류">
            <Select style={{ width: 100 }} value={selectedFilter1} options={assetType} onChange={onChangeAsset1} />
          </Descriptions.Item>
          <Descriptions.Item label="소분류">
            <Select style={{ width: 100 }} value={selectedFilter2} options={selectedFilter1 === "ALL" ? allList : selectedFilter1 === "hwList" ? hwList : swList} onChange={onChangeAsset2} />
          </Descriptions.Item>
          <Descriptions.Item label="검색대상">
            <Select style={{ width: 150 }} value={selectedFilter3} options={assetList} onChange={onChangeAsset3} />
          </Descriptions.Item>
        </Descriptions>
      </Space>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 20 }}>
        <RangePicker onChange={onChange} style={{ marginRight: 20, width: 340 }} format="YYYYMMDD" value={dateSetting} />
        <Input
          value={searchValue}
          onChange={(event) => {
            setSearchValue(event.target.value);
          }}
        />
        <Button className="primaryBtn" style={{ marginLeft: 20 }} onClick={reqHistoryData}>
          검색
        </Button>
      </div>
      <Table loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false} bordered rowClassName="editable-row" columns={columns} dataSource={searchList} />
    </Content>
  );
};

export default History;

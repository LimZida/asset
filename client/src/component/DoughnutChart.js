import React, { useRef, useEffect, useState } from "react";
import { Pie } from "@antv/g2plot";
import { Select } from "antd";
import insertCss from "insert-css";
import ReactApexChart from "react-apexcharts";
import DashboardTable from "../component/DashboardTable.js";

insertCss(`
  .hover-g2plot-pie {
    cursor: pointer;
    margin-bottom: 2px;
  }
  .hover-g2plot-pie:hover {
    background-color: #EEF7FF;
  }
`);

const DoughnutChart = (props) => {
  const [data, setData] = useState(props.data || []);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const chartContainerRef = useRef();
  const chartRef = useRef();
  const [assetCode, setAssetCode] = useState("ALL");
  const [series, setSeries] = useState(props.data.map((item) => item.totalCnt) || []);
  const [labels, setLabels] = useState(props.data.map((item) => item.label) || []);
  const [donutLabel, setDonutLabel] = useState("Total");
  const [selectList, setSelectList] = useState([{ value: "ALL", label: "전체" }, ...(props.data || []).map((item) => ({ value: item.assetCodeName, label: item.label }))]);

  useEffect(() => {
    if (chartContainerRef.current) {
      if (!chartRef.current) {
        chartRef.current = new Pie(chartContainerRef.current, {
          data,
          angleField: "totalCnt",
          colorField: "assetCodeName",
          padding: 20,
          interactions: [
            {
              type: "element-selected",
            },
            {
              type: "element-active",
            },
          ],
        });
        chartRef.current.render();
        chartRef.current.on("element:click", (evt) => {
          const { data: clickData } = evt;
          const selectedKey = clickData.data.assetCodeName;
          const selectedRow = data.find((item) => item.assetCodeName === selectedKey);
          if (selectedRow) {
            setSelectedRowKeys([selectedRow.assetCodeName]);
          }
        });
      } else {
        chartRef.current.update({ data });
      }
    }
  }, [data]);

  useEffect(() => {
    if (props.data && props.data.length > 0) {
      setData(props.data);
      setSeries(props.data.map((item) => item.totalCnt));
      setLabels(props.data.map((item) => item.label));
      setSelectList([{ value: "ALL", label: "전체" }, ...props.data.map((item) => ({ value: item.assetCodeName, label: item.label }))]);
    }
  }, [props.data]);

  useEffect(() => {
    if (assetCode === "ALL") {
      setData(props.data);
      setSeries(props.data.map((item) => item.totalCnt));
      setLabels(props.data.map((item) => item.label));
      setDonutLabel("Total");
    } else {
      const item = props.data.find((item) => item.assetCodeName === assetCode);
      if (item) {
        setSeries([item.allocationAsset, item.notAllocationAsset]);
        setLabels(["할당", "잉여"]);
        setDonutLabel(item.assetCodeName);
      }
    }
  }, [assetCode, props.data]);

  const donutData = {
    series: series.length > 0 ? series : [0],
    options: {
      chart: { type: "donut" },
      legend: { position: "right", horizontalAlign: "left" },
      responsive: [{ breakpoint: 480 }],
      plotOptions: {
        pie: {
          donut: {
            labels: {
              show: true,
              total: {
                showAlways: true,
                show: true,
                label: donutLabel,
                fontSize: "20px",
                fontWeight: 700,
                color: "black",
              },
              value: { fontSize: "20px", show: true, color: "black", fontWeight: 700 },
            },
          },
        },
      },
      labels: labels.length > 0 ? labels : ["No Data"],
    },
  };

  return (
    <div>
      <div id="chart" style={{ marginBottom: 20 }}>
        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", marginBottom: 20 }}>
          <div style={{ fontWeight: "bold", marginRight: 20, fontSize: 20 }}>{props.type} 보유 자산 그래프</div>
          <Select
            options={selectList}
            style={{ width: 100 }}
            value={assetCode}
            onChange={(value) => {
              setAssetCode(value);
              console.log("Selected asset code:", value);
            }}
          />
        </div>
        <ReactApexChart series={donutData.series} options={donutData.options} type="donut" width="400" style={{ display: "flex", justifyContent: "center" }} />
      </div>
      <DashboardTable data={props.data} type={props.type}></DashboardTable>
    </div>
  );
};

export default DoughnutChart;

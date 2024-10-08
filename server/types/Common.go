package types

// Page 分页数据封装实体
type Page struct {
	PageNum    int64         `json:"pageNum"`
	PageSize   int64         `json:"pageSize"`
	TotalPage  int64         `json:"totalPage"`
	TotalCount int64         `json:"totalCount"`
	TotalOut   float64       `json:"totalOut"`
	TotalIn    float64       `json:"totalIn"`
	NotInOut   float64       `json:"notInOut"`
	PageData   []interface{} `json:"pageData"`
}

// Result 返回数据同一封装
type Result struct {
	Code    int64  `json:"c"`
	Message string `json:"m"`
	Data    any    `json:"d"`
}

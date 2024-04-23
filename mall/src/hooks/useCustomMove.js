import {createSearchParams, useNavigate, useSearchParams} from "react-router-dom";
import {useCallback} from "react";


const getNum = (param, defaultValue) => {

    if (!param) {
        return defaultValue
    }
    return parseInt(param)
}


const useCustomMove = () => {

    const navigate = useNavigate()
    const [queryParams] = useSearchParams()
    const page = queryParams.get("page") ? parseInt(queryParams.get("page")) : 1
    const size = queryParams.get("size") ? parseInt(queryParams.get("size")) : 10

    // page=3&size=10 만드는 것은 함수가 있습니다.
    const queryDefault = createSearchParams({page, size}).toString()

    const moveToList = (pageParam) => {
        let queryStr = ""

        if (pageParam) {
            const pageNum = getNum(pageParam.page, 1)
            const sizeNum = getNum(pageParam.size, 10)
            queryStr = createSearchParams({page:pageNum, size:sizeNum}).toString()
        } else {
            queryStr = queryDefault
        }

        navigate({pathname: `../list`, search:queryStr})
    }

    const moveToModify = useCallback((num) => {
        console.log(queryDefault)
        navigate({pathname: `../modify/${num}`, search:queryDefault}
        , [page, size])
    })

    return {moveToList, moveToModify, page, size}
}

export default useCustomMove

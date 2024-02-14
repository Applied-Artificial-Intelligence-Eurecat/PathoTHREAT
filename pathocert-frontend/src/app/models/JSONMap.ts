export class JSONMap {
    isTerminal: boolean;
    terminalValue: string[];
    treeValue: Map<string, JSONMap[]>;
    isList: boolean = false;

    constructor (terminal: boolean, terminalValue?: string | string[], treeValue?: Map<string, JSONMap[]>) {
        this.isTerminal = terminal;
        if(typeof terminalValue === "string"){
            this.terminalValue = [terminalValue];
        }else{
            this.terminalValue = terminalValue;
        }
        this.treeValue = treeValue;
    }

    set(key: string, value: JSONMap[]) {
        this.treeValue.set(key, value);
    }

    get(key: string): JSONMap[] {
        return this.treeValue.get(key);
    }

    setValue(value: string | string[]) {
        if(typeof value === "string"){
            this.terminalValue = [value];
        }else{
            this.terminalValue = value;
        }
    }

    getValue(): string[] {
        return this.terminalValue;
    }

    toJSONString(): string {
        if (this.isTerminal){
            if (this.terminalValue.length == 0){
                return "";
            } else if (this.terminalValue.length == 1 && !this.isList){
                return "\"" + this.terminalValue[0] + "\"";
            } else {
                var st: string = "[";
                this.terminalValue.forEach((value) => {
                    st += "\"" + value + "\","
                });
                st = st.slice(0, -1);
                st += "]"
                return st
            }
        } else {
            var st = "{";
            this.treeValue.forEach((value: JSONMap[], key: string) => {
                if (value.length == 0){
                    st += "\"" + key + "\": \"\",";
                } else if (value.length == 1 && !this.isList){
                    st += "\"" + key + "\": " + value[0].toJSONString() + ",";
                } else {
                    var childSt: string = "[";
                    value.forEach((value) => {
                        childSt += "\"" + value.toJSONString() + "\","
                    });
                    childSt = childSt.slice(0, -1);
                    childSt += "],"
                    st += childSt;
                }
            });
            st = st.slice(0, -1);
            st += "}"
            return st;
        }
    }
}
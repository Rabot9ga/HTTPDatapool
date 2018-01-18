package ru.sbt.util.HTTPDatapool.httpapi;

import lombok.Builder;
import ru.sbt.util.HTTPDatapool.paramsContainer.dto.RequestType;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;

@Builder
public class ParametersTable implements Serializable {

    RequestType type;
    String tableName;
    Set<String> columnsName;
    String scriptName;

    public Stream<String> getColumnsNameStream(){
        return columnsName.stream();
    }


    public RequestType getType() {
        return this.type;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Set<String> getColumnsName() {
        return this.columnsName;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnsName(Set<String> columnsName) {
        this.columnsName = columnsName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ParametersTable)) return false;
        final ParametersTable other = (ParametersTable) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$tableName = this.getTableName();
        final Object other$tableName = other.getTableName();
        if (this$tableName == null ? other$tableName != null : !this$tableName.equals(other$tableName)) return false;
        final Object this$columnsName = this.getColumnsName();
        final Object other$columnsName = other.getColumnsName();
        if (this$columnsName == null ? other$columnsName != null : !this$columnsName.equals(other$columnsName))
            return false;
        final Object this$scriptName = this.getScriptName();
        final Object other$scriptName = other.getScriptName();
        if (this$scriptName == null ? other$scriptName != null : !this$scriptName.equals(other$scriptName))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $tableName = this.getTableName();
        result = result * PRIME + ($tableName == null ? 43 : $tableName.hashCode());
        final Object $columnsName = this.getColumnsName();
        result = result * PRIME + ($columnsName == null ? 43 : $columnsName.hashCode());
        final Object $scriptName = this.getScriptName();
        result = result * PRIME + ($scriptName == null ? 43 : $scriptName.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ParametersTable;
    }

    public String toString() {
        return "ParametersTable(type=" + this.getType() + ", tableName=" + this.getTableName() + ", columnsName=" + this.getColumnsName() + ", scriptName=" + this.getScriptName() + ")";
    }
}

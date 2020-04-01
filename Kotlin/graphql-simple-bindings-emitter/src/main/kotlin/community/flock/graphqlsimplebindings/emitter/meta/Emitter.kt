package community.flock.graphqlsimplebindings.emitter.meta

import community.flock.graphqlsimplebindings.DefinitionEmitter
import community.flock.graphqlsimplebindings.EnumEmitter
import community.flock.graphqlsimplebindings.FieldDefinitionEmitter
import community.flock.graphqlsimplebindings.TypeEmitter
import community.flock.graphqlsimplebindings.emitter.TypeScriptEmitter.emitEnumTypeDefinition
import community.flock.graphqlsimplebindings.emitter.TypeScriptEmitter.emitInterfaceTypeDefinition
import community.flock.graphqlsimplebindings.exceptions.DefinitionEmitterException
import community.flock.graphqlsimplebindings.exceptions.TypeEmitterException
import graphql.language.*
import graphql.schema.idl.TypeInfo

abstract class Emitter : DefinitionEmitter, EnumEmitter, FieldDefinitionEmitter, TypeEmitter {

    open fun emitDocument(document: Document): String = document.definitions
            .mapNotNull { it.emitDefinition() }
            .joinToString("\n")

    private fun Definition<Definition<*>>.emitDefinition() = when (this) {
        is ObjectTypeDefinition -> emitObjectTypeDefinition()
        is ScalarTypeDefinition -> emitScalarTypeDefinition()
        is InputObjectTypeDefinition -> emitInputObjectTypeDefinition()
        is EnumTypeDefinition -> emitEnumTypeDefinition()
        is InterfaceTypeDefinition -> emitInterfaceTypeDefinition()
        else -> throw DefinitionEmitterException(this)
    }

    protected val Type<Type<*>>.value: String get() = TypeInfo.typeInfo(this).name

    protected fun Type<Type<*>>.emitType(): String = when (this) {
        is NonNullType -> type.toNonNullableType()
        is ListType -> nullableListOf(type)
        is TypeName -> value.toNullable()
        else -> throw TypeEmitterException(this)
    }

    private fun Type<Type<*>>.toNonNullableType(): String = when (this) {
        is ListType -> nonNullableListOf(type)
        is TypeName -> value.toNonNullable()
        else -> throw TypeEmitterException(this)
    }

}

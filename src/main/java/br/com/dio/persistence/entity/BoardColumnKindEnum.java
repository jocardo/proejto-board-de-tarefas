package br.com.dio.persistence.entity;


Public enum BoardColumnKindEnum {
    INITIAL, FINAL, CANCEL, PENDEING;


    public static BoardColumnKindEnum findByName(final String name) {
        return Stream.of(BoardColumnKindEnum.values())
                .filter(b -> b.name().equalsIgnoreCase(name))
                .findFirst().orElseThrow() 
    }
        
}

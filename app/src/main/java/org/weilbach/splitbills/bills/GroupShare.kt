package org.weilbach.splitbills.bills

data class GroupShare(val groupName: String,
                      val subject: String,
                      val content: String,
                      val appendix: String,
                      val emails: Array<String>)